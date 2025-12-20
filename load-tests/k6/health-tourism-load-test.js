import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';
import { randomString, randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

/**
 * k6 Performance Test Suite for Health Tourism Platform
 * Tests 100,000 concurrent users to find bottlenecks
 */

// Custom metrics
const errorRate = new Rate('errors');
const loginDuration = new Trend('login_duration');
const searchDuration = new Trend('search_duration');
const bookingDuration = new Trend('booking_duration');
const apiCalls = new Counter('api_calls');

// Test configuration
export const options = {
    scenarios: {
        // Smoke test
        smoke: {
            executor: 'constant-vus',
            vus: 10,
            duration: '1m',
            startTime: '0s',
            tags: { test_type: 'smoke' },
        },
        // Load test - gradual ramp up
        load: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '2m', target: 1000 },   // Ramp up to 1000 users
                { duration: '5m', target: 1000 },   // Stay at 1000
                { duration: '2m', target: 5000 },   // Ramp up to 5000
                { duration: '5m', target: 5000 },   // Stay at 5000
                { duration: '2m', target: 10000 },  // Ramp up to 10000
                { duration: '5m', target: 10000 },  // Stay at 10000
                { duration: '2m', target: 0 },      // Ramp down
            ],
            startTime: '1m',
            tags: { test_type: 'load' },
        },
        // Stress test - find breaking point
        stress: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '2m', target: 10000 },
                { duration: '5m', target: 10000 },
                { duration: '2m', target: 50000 },
                { duration: '5m', target: 50000 },
                { duration: '2m', target: 100000 }, // 100K users
                { duration: '5m', target: 100000 },
                { duration: '5m', target: 0 },
            ],
            startTime: '25m',
            tags: { test_type: 'stress' },
        },
        // Spike test
        spike: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '10s', target: 50000 }, // Sudden spike
                { duration: '1m', target: 50000 },
                { duration: '10s', target: 0 },
            ],
            startTime: '50m',
            tags: { test_type: 'spike' },
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<2000', 'p(99)<5000'], // 95% under 2s, 99% under 5s
        http_req_failed: ['rate<0.01'],                   // Error rate < 1%
        errors: ['rate<0.05'],                            // Custom error rate < 5%
        login_duration: ['p(95)<1000'],                   // Login under 1s
        search_duration: ['p(95)<500'],                   // Search under 500ms
        booking_duration: ['p(95)<3000'],                 // Booking under 3s
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// Test data
const testUsers = [];
for (let i = 0; i < 1000; i++) {
    testUsers.push({
        email: `loadtest${i}@example.com`,
        password: 'LoadTest123!',
    });
}

// Helper functions
function getRandomUser() {
    return testUsers[randomIntBetween(0, testUsers.length - 1)];
}

function getHeaders(token = null) {
    const headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    };
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    return headers;
}

// Setup - runs once before tests
export function setup() {
    console.log('Setting up load test...');
    
    // Register test users (optional - can be pre-seeded)
    const registeredUsers = [];
    for (let i = 0; i < 10; i++) {
        const user = testUsers[i];
        const res = http.post(`${BASE_URL}/api/auth/register`, JSON.stringify({
            email: user.email,
            password: user.password,
            firstName: 'Load',
            lastName: `Test${i}`,
        }), { headers: getHeaders() });
        
        if (res.status === 200) {
            const body = JSON.parse(res.body);
            registeredUsers.push({ ...user, token: body.accessToken });
        }
    }
    
    return { users: registeredUsers };
}

// Main test function
export default function(data) {
    const user = getRandomUser();
    
    group('Authentication Flow', () => {
        // Login
        const loginStart = Date.now();
        const loginRes = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify({
            email: user.email,
            password: user.password,
        }), { headers: getHeaders() });
        
        loginDuration.add(Date.now() - loginStart);
        apiCalls.add(1);
        
        const loginSuccess = check(loginRes, {
            'login status is 200': (r) => r.status === 200,
            'login has token': (r) => {
                try {
                    const body = JSON.parse(r.body);
                    return body.accessToken !== undefined;
                } catch {
                    return false;
                }
            },
        });
        
        errorRate.add(!loginSuccess);
        
        if (!loginSuccess) {
            return;
        }
        
        const token = JSON.parse(loginRes.body).accessToken;
        
        // Search hospitals
        group('Search Flow', () => {
            const searchStart = Date.now();
            const searchRes = http.get(`${BASE_URL}/api/hospitals?city=Istanbul&specialty=Dental`, {
                headers: getHeaders(token),
            });
            
            searchDuration.add(Date.now() - searchStart);
            apiCalls.add(1);
            
            check(searchRes, {
                'search status is 200': (r) => r.status === 200,
                'search returns array': (r) => {
                    try {
                        return Array.isArray(JSON.parse(r.body));
                    } catch {
                        return false;
                    }
                },
            });
        });
        
        // Get doctors
        group('Doctor Listing', () => {
            const doctorsRes = http.get(`${BASE_URL}/api/doctors?hospitalId=1`, {
                headers: getHeaders(token),
            });
            apiCalls.add(1);
            
            check(doctorsRes, {
                'doctors status is 200': (r) => r.status === 200,
            });
        });
        
        // Create booking
        group('Booking Flow', () => {
            const bookingStart = Date.now();
            const bookingRes = http.post(`${BASE_URL}/api/reservations`, JSON.stringify({
                hospitalId: randomIntBetween(1, 10),
                doctorId: randomIntBetween(1, 50),
                procedureType: 'DENTAL_IMPLANT',
                appointmentDate: '2024-06-15',
                notes: 'Load test booking',
            }), { headers: getHeaders(token) });
            
            bookingDuration.add(Date.now() - bookingStart);
            apiCalls.add(1);
            
            check(bookingRes, {
                'booking status is 200 or 201': (r) => r.status === 200 || r.status === 201,
            });
        });
        
        // Get user profile
        group('Profile Access', () => {
            const profileRes = http.get(`${BASE_URL}/api/users/me`, {
                headers: getHeaders(token),
            });
            apiCalls.add(1);
            
            check(profileRes, {
                'profile status is 200': (r) => r.status === 200,
            });
        });
    });
    
    sleep(randomIntBetween(1, 3)); // Think time
}

// Teardown - runs once after tests
export function teardown(data) {
    console.log('Cleaning up load test...');
    // Cleanup logic if needed
}

// Handle test results
export function handleSummary(data) {
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'load-test-results.json': JSON.stringify(data),
        'load-test-results.html': htmlReport(data),
    };
}

// Text summary helper
function textSummary(data, options) {
    const { metrics, root_group } = data;
    
    let summary = '\n=== LOAD TEST SUMMARY ===\n\n';
    
    // Key metrics
    summary += `Total Requests: ${metrics.http_reqs?.values?.count || 0}\n`;
    summary += `Failed Requests: ${metrics.http_req_failed?.values?.passes || 0}\n`;
    summary += `Error Rate: ${(metrics.errors?.values?.rate * 100 || 0).toFixed(2)}%\n\n`;
    
    summary += `Response Times (p95):\n`;
    summary += `  - Overall: ${metrics.http_req_duration?.values?.['p(95)']?.toFixed(2) || 0}ms\n`;
    summary += `  - Login: ${metrics.login_duration?.values?.['p(95)']?.toFixed(2) || 0}ms\n`;
    summary += `  - Search: ${metrics.search_duration?.values?.['p(95)']?.toFixed(2) || 0}ms\n`;
    summary += `  - Booking: ${metrics.booking_duration?.values?.['p(95)']?.toFixed(2) || 0}ms\n`;
    
    return summary;
}

// HTML report helper
function htmlReport(data) {
    return `
<!DOCTYPE html>
<html>
<head>
    <title>Health Tourism Load Test Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .metric { margin: 10px 0; padding: 10px; background: #f5f5f5; }
        .pass { color: green; }
        .fail { color: red; }
    </style>
</head>
<body>
    <h1>Health Tourism Platform - Load Test Report</h1>
    <h2>Test Summary</h2>
    <div class="metric">
        <strong>Total Requests:</strong> ${data.metrics.http_reqs?.values?.count || 0}
    </div>
    <div class="metric">
        <strong>Error Rate:</strong> ${(data.metrics.errors?.values?.rate * 100 || 0).toFixed(2)}%
    </div>
    <div class="metric">
        <strong>P95 Response Time:</strong> ${data.metrics.http_req_duration?.values?.['p(95)']?.toFixed(2) || 0}ms
    </div>
    <h2>Thresholds</h2>
    ${Object.entries(data.thresholds || {}).map(([name, result]) => `
        <div class="metric ${result.ok ? 'pass' : 'fail'}">
            ${name}: ${result.ok ? 'PASSED' : 'FAILED'}
        </div>
    `).join('')}
</body>
</html>`;
}





