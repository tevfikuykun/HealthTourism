import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

// Stress test - push system beyond normal capacity
export const options = {
    stages: [
        { duration: '1m', target: 500 },   // Rapid ramp up to 500 users
        { duration: '3m', target: 500 },   // Stay at 500 users
        { duration: '1m', target: 1000 },   // Spike to 1000 users
        { duration: '2m', target: 1000 },    // Stay at 1000 users
        { duration: '2m', target: 0 },      // Ramp down
    ],
    thresholds: {
        http_req_duration: ['p(95)<2000'],  // Relaxed for stress test
        http_req_failed: ['rate<0.05'],     // Allow up to 5% errors
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
    const randomEmail = `stress${Math.floor(Math.random() * 1000000)}@example.com`;
    
    // Register
    const registerPayload = JSON.stringify({
        email: randomEmail,
        password: 'Test123!@#',
        firstName: 'Stress',
        lastName: 'Test',
        phone: '+905551234567',
        country: 'Turkey'
    });
    
    const registerRes = http.post(`${BASE_URL}/api/auth/register`, registerPayload, {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: 'Register' },
    });
    
    const registerSuccess = check(registerRes, {
        'register status is 200 or 429': (r) => r.status === 200 || r.status === 429,
    });
    
    errorRate.add(!registerSuccess);
    
    // Multiple concurrent requests
    const requests = [
        ['GET', `${BASE_URL}/api/hospitals`],
        ['GET', `${BASE_URL}/api/doctors`],
        ['GET', `${BASE_URL}/api/accommodations`],
    ];
    
    const responses = http.batch(requests);
    
    responses.forEach((res, index) => {
        check(res, {
            [`request ${index} status is 200 or 429`]: (r) => r.status === 200 || r.status === 429,
        });
    });
    
    sleep(0.5);
}

