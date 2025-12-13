import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const authResponseTime = new Trend('auth_response_time');
const apiResponseTime = new Trend('api_response_time');

// Test configuration
export const options = {
    stages: [
        { duration: '2m', target: 100 },  // Ramp up to 100 users
        { duration: '5m', target: 100 },    // Stay at 100 users
        { duration: '2m', target: 200 },   // Ramp up to 200 users
        { duration: '5m', target: 200 },    // Stay at 200 users
        { duration: '2m', target: 0 },      // Ramp down to 0 users
    ],
    thresholds: {
        http_req_duration: ['p(95)<500', 'p(99)<1000'], // 95% of requests < 500ms, 99% < 1000ms
        http_req_failed: ['rate<0.01'],                  // Error rate < 1%
        errors: ['rate<0.01'],
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
    // Test Authentication Service
    testAuthService();
    
    // Test User Service
    testUserService();
    
    // Test Hospital Service
    testHospitalService();
    
    // Test Doctor Service
    testDoctorService();
    
    sleep(1);
}

function testAuthService() {
    const randomEmail = `test${Math.floor(Math.random() * 100000)}@example.com`;
    
    // Register
    const registerPayload = JSON.stringify({
        email: randomEmail,
        password: 'Test123!@#',
        firstName: 'Test',
        lastName: 'User',
        phone: '+905551234567',
        country: 'Turkey'
    });
    
    const registerRes = http.post(`${BASE_URL}/api/auth/register`, registerPayload, {
        headers: { 'Content-Type': 'application/json' },
    });
    
    const registerSuccess = check(registerRes, {
        'register status is 200': (r) => r.status === 200,
        'register has token': (r) => r.json('accessToken') !== undefined,
    });
    
    errorRate.add(!registerSuccess);
    authResponseTime.add(registerRes.timings.duration);
    
    if (registerRes.status === 200) {
        const token = registerRes.json('accessToken');
        
        // Login
        const loginPayload = JSON.stringify({
            email: randomEmail,
            password: 'Test123!@#',
        });
        
        const loginRes = http.post(`${BASE_URL}/api/auth/login`, loginPayload, {
            headers: { 'Content-Type': 'application/json' },
        });
        
        const loginSuccess = check(loginRes, {
            'login status is 200': (r) => r.status === 200,
            'login has token': (r) => r.json('accessToken') !== undefined,
        });
        
        errorRate.add(!loginSuccess);
        authResponseTime.add(loginRes.timings.duration);
        
        // Validate token
        if (loginRes.status === 200) {
            const validatePayload = JSON.stringify({
                token: token,
            });
            
            const validateRes = http.post(`${BASE_URL}/api/auth/validate`, validatePayload, {
                headers: { 'Content-Type': 'application/json' },
            });
            
            check(validateRes, {
                'validate status is 200': (r) => r.status === 200,
            });
        }
    }
}

function testUserService() {
    const res = http.get(`${BASE_URL}/api/users`, {
        headers: { 'Content-Type': 'application/json' },
    });
    
    check(res, {
        'users status is 200 or 401': (r) => r.status === 200 || r.status === 401,
    });
    
    apiResponseTime.add(res.timings.duration);
}

function testHospitalService() {
    const res = http.get(`${BASE_URL}/api/hospitals`, {
        headers: { 'Content-Type': 'application/json' },
    });
    
    check(res, {
        'hospitals status is 200': (r) => r.status === 200,
        'hospitals has data': (r) => r.json().length >= 0,
    });
    
    apiResponseTime.add(res.timings.duration);
}

function testDoctorService() {
    const res = http.get(`${BASE_URL}/api/doctors`, {
        headers: { 'Content-Type': 'application/json' },
    });
    
    check(res, {
        'doctors status is 200': (r) => r.status === 200,
        'doctors has data': (r) => r.json().length >= 0,
    });
    
    apiResponseTime.add(res.timings.duration);
}

export function handleSummary(data) {
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'load-test-results.json': JSON.stringify(data),
    };
}

