import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Counter, Gauge } from 'k6/metrics';

/**
 * Bottleneck Analysis Test
 * Identifies where the system breaks under load
 */

// Metrics for bottleneck detection
const dbQueryTime = new Trend('db_query_time');
const cacheHitRate = new Gauge('cache_hit_rate');
const connectionPoolUsage = new Gauge('connection_pool_usage');
const memoryUsage = new Gauge('memory_usage');
const cpuUsage = new Gauge('cpu_usage');
const queueDepth = new Gauge('queue_depth');

export const options = {
    scenarios: {
        // Database bottleneck test
        database_stress: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 100,
            maxVUs: 10000,
            stages: [
                { duration: '1m', target: 100 },
                { duration: '2m', target: 500 },
                { duration: '2m', target: 1000 },
                { duration: '2m', target: 2000 },
                { duration: '1m', target: 0 },
            ],
            exec: 'testDatabaseOperations',
            tags: { bottleneck: 'database' },
        },
        // Cache bottleneck test
        cache_stress: {
            executor: 'ramping-arrival-rate',
            startRate: 50,
            timeUnit: '1s',
            preAllocatedVUs: 200,
            maxVUs: 20000,
            stages: [
                { duration: '1m', target: 500 },
                { duration: '2m', target: 2000 },
                { duration: '2m', target: 5000 },
                { duration: '1m', target: 0 },
            ],
            exec: 'testCacheOperations',
            tags: { bottleneck: 'cache' },
        },
        // API Gateway bottleneck test
        gateway_stress: {
            executor: 'ramping-arrival-rate',
            startRate: 100,
            timeUnit: '1s',
            preAllocatedVUs: 500,
            maxVUs: 50000,
            stages: [
                { duration: '1m', target: 1000 },
                { duration: '2m', target: 5000 },
                { duration: '2m', target: 10000 },
                { duration: '2m', target: 20000 },
                { duration: '1m', target: 0 },
            ],
            exec: 'testGatewayThroughput',
            tags: { bottleneck: 'gateway' },
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<3000'],
        http_req_failed: ['rate<0.1'],
        db_query_time: ['p(95)<500'],
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// Database-heavy operations
export function testDatabaseOperations() {
    const endpoints = [
        '/api/hospitals?page=0&size=100',
        '/api/doctors?specialty=Dental&page=0&size=50',
        '/api/reservations/search?status=PENDING',
        '/api/users/search?country=Germany',
        '/api/medical-documents/user/1',
    ];
    
    const endpoint = endpoints[Math.floor(Math.random() * endpoints.length)];
    
    const start = Date.now();
    const res = http.get(`${BASE_URL}${endpoint}`, {
        headers: { 'Content-Type': 'application/json' },
        tags: { operation: 'db_read' },
    });
    
    dbQueryTime.add(Date.now() - start);
    
    check(res, {
        'db operation successful': (r) => r.status === 200,
        'response time acceptable': (r) => r.timings.duration < 1000,
    });
    
    // Collect server metrics if available
    collectServerMetrics();
}

// Cache-heavy operations
export function testCacheOperations() {
    const cacheableEndpoints = [
        '/api/hospitals/1',
        '/api/doctors/1',
        '/api/packages/featured',
        '/api/currency/rates',
        '/api/faq/popular',
    ];
    
    const endpoint = cacheableEndpoints[Math.floor(Math.random() * cacheableEndpoints.length)];
    
    const res = http.get(`${BASE_URL}${endpoint}`, {
        headers: { 'Content-Type': 'application/json' },
        tags: { operation: 'cache_read' },
    });
    
    // Check cache header
    const cacheHit = res.headers['X-Cache-Hit'] === 'true';
    cacheHitRate.add(cacheHit ? 1 : 0);
    
    check(res, {
        'cache operation successful': (r) => r.status === 200,
        'cached response fast': (r) => cacheHit ? r.timings.duration < 50 : true,
    });
}

// Gateway throughput test
export function testGatewayThroughput() {
    const res = http.get(`${BASE_URL}/api/health`, {
        headers: { 'Content-Type': 'application/json' },
        tags: { operation: 'gateway' },
    });
    
    check(res, {
        'gateway responsive': (r) => r.status === 200,
        'gateway fast': (r) => r.timings.duration < 100,
    });
}

// Collect server-side metrics
function collectServerMetrics() {
    try {
        const metricsRes = http.get(`${BASE_URL}/actuator/metrics`, {
            tags: { operation: 'metrics' },
        });
        
        if (metricsRes.status === 200) {
            const metrics = JSON.parse(metricsRes.body);
            
            // JVM Memory
            const memRes = http.get(`${BASE_URL}/actuator/metrics/jvm.memory.used`);
            if (memRes.status === 200) {
                const memData = JSON.parse(memRes.body);
                memoryUsage.add(memData.measurements?.[0]?.value || 0);
            }
            
            // Connection Pool
            const poolRes = http.get(`${BASE_URL}/actuator/metrics/hikaricp.connections.active`);
            if (poolRes.status === 200) {
                const poolData = JSON.parse(poolRes.body);
                connectionPoolUsage.add(poolData.measurements?.[0]?.value || 0);
            }
        }
    } catch (e) {
        // Metrics endpoint might not be available
    }
}

export function handleSummary(data) {
    const bottlenecks = analyzeBottlenecks(data);
    
    return {
        'stdout': generateBottleneckReport(data, bottlenecks),
        'bottleneck-analysis.json': JSON.stringify({ data, bottlenecks }),
    };
}

function analyzeBottlenecks(data) {
    const bottlenecks = [];
    const metrics = data.metrics;
    
    // Database bottleneck detection
    if (metrics.db_query_time?.values?.['p(95)'] > 500) {
        bottlenecks.push({
            type: 'DATABASE',
            severity: 'HIGH',
            metric: `P95 DB Query Time: ${metrics.db_query_time.values['p(95)'].toFixed(2)}ms`,
            recommendation: 'Consider adding database indexes, connection pooling, or read replicas',
        });
    }
    
    // Cache bottleneck detection
    if (metrics.cache_hit_rate?.values?.value < 0.8) {
        bottlenecks.push({
            type: 'CACHE',
            severity: 'MEDIUM',
            metric: `Cache Hit Rate: ${(metrics.cache_hit_rate.values.value * 100).toFixed(2)}%`,
            recommendation: 'Increase cache size or TTL, review cache invalidation strategy',
        });
    }
    
    // Connection pool bottleneck
    if (metrics.connection_pool_usage?.values?.value > 80) {
        bottlenecks.push({
            type: 'CONNECTION_POOL',
            severity: 'HIGH',
            metric: `Connection Pool Usage: ${metrics.connection_pool_usage.values.value}%`,
            recommendation: 'Increase connection pool size or optimize query performance',
        });
    }
    
    // Gateway bottleneck
    const gatewayP95 = metrics.http_req_duration?.values?.['p(95)'];
    if (gatewayP95 > 2000) {
        bottlenecks.push({
            type: 'API_GATEWAY',
            severity: 'HIGH',
            metric: `P95 Response Time: ${gatewayP95.toFixed(2)}ms`,
            recommendation: 'Scale API Gateway horizontally, enable caching, optimize rate limiting',
        });
    }
    
    // Error rate bottleneck
    const errorRate = metrics.http_req_failed?.values?.rate;
    if (errorRate > 0.05) {
        bottlenecks.push({
            type: 'ERROR_RATE',
            severity: 'CRITICAL',
            metric: `Error Rate: ${(errorRate * 100).toFixed(2)}%`,
            recommendation: 'Check application logs, increase resources, implement circuit breakers',
        });
    }
    
    return bottlenecks;
}

function generateBottleneckReport(data, bottlenecks) {
    let report = '\n=== BOTTLENECK ANALYSIS REPORT ===\n\n';
    
    report += 'Test Summary:\n';
    report += `  Total Requests: ${data.metrics.http_reqs?.values?.count || 0}\n`;
    report += `  Error Rate: ${((data.metrics.http_req_failed?.values?.rate || 0) * 100).toFixed(2)}%\n`;
    report += `  P95 Response Time: ${data.metrics.http_req_duration?.values?.['p(95)']?.toFixed(2) || 0}ms\n\n`;
    
    if (bottlenecks.length === 0) {
        report += '✅ No significant bottlenecks detected!\n';
    } else {
        report += `⚠️ ${bottlenecks.length} Bottleneck(s) Detected:\n\n`;
        
        bottlenecks.forEach((b, i) => {
            report += `${i + 1}. [${b.severity}] ${b.type}\n`;
            report += `   Metric: ${b.metric}\n`;
            report += `   Recommendation: ${b.recommendation}\n\n`;
        });
    }
    
    return report;
}








