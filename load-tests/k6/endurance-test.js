import http from 'k6/http';
import { check, sleep } from 'k6';

// Endurance test - long duration test
export const options = {
    stages: [
        { duration: '5m', target: 50 },   // Ramp up
        { duration: '30m', target: 50 },   // Stay for 30 minutes
        { duration: '5m', target: 0 },     // Ramp down
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.01'],
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
    const randomEmail = `endurance${Math.floor(Math.random() * 100000)}@example.com`;
    
    // Register
    const registerPayload = JSON.stringify({
        email: randomEmail,
        password: 'Test123!@#',
        firstName: 'Endurance',
        lastName: 'Test',
        phone: '+905551234567',
        country: 'Turkey'
    });
    
    const registerRes = http.post(`${BASE_URL}/api/auth/register`, registerPayload, {
        headers: { 'Content-Type': 'application/json' },
    });
    
    check(registerRes, {
        'register status is 200': (r) => r.status === 200,
    });
    
    // Test various endpoints
    const endpoints = [
        '/api/hospitals',
        '/api/doctors',
        '/api/accommodations',
        '/api/packages',
    ];
    
    endpoints.forEach(endpoint => {
        const res = http.get(`${BASE_URL}${endpoint}`, {
            headers: { 'Content-Type': 'application/json' },
        });
        
        check(res, {
            [`${endpoint} status is 200`]: (r) => r.status === 200,
        });
    });
    
    sleep(2);
}

