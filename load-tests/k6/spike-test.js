import http from 'k6/http';
import { check, sleep } from 'k6';

// Spike test - sudden increase in load
export const options = {
    stages: [
        { duration: '1m', target: 100 },    // Normal load
        { duration: '30s', target: 2000 }, // Sudden spike
        { duration: '1m', target: 2000 },  // Stay at spike
        { duration: '30s', target: 100 },  // Back to normal
        { duration: '1m', target: 100 },   // Stay normal
    ],
    thresholds: {
        http_req_duration: ['p(95)<3000'],
        http_req_failed: ['rate<0.1'],
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
    // Simple GET requests to test spike handling
    const res = http.get(`${BASE_URL}/api/hospitals`, {
        headers: { 'Content-Type': 'application/json' },
    });
    
    check(res, {
        'status is 200 or 429': (r) => r.status === 200 || r.status === 429,
    });
    
    sleep(1);
}

