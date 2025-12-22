package com.healthtourism.apigateway.filter;

// Bucket4j temporarily disabled - version not available in Maven Central
// import io.github.bucket4j.Bandwidth;
// import io.github.bucket4j.Bucket;
// import io.github.bucket4j.Refill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.DefaultReactiveScriptExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RedisRateLimitingFilter extends AbstractGatewayFilterFactory<RedisRateLimitingFilter.Config> {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisRateLimitingFilter.class);
    // private final Map<String, Bucket> cache = new ConcurrentHashMap<>(); // Temporarily disabled
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    
    public RedisRateLimitingFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        // Rate limiting temporarily disabled - Bucket4j not available
        // Will be re-enabled when Bucket4j dependency is available
        return (exchange, chain) -> {
            // Allow all requests for now
            return chain.filter(exchange);
        };
    }
    
    private String getClientId(org.springframework.web.server.ServerWebExchange exchange) {
        String ip = exchange.getRequest().getRemoteAddress() != null 
            ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
            : "unknown";
        
        // Try to get user ID from JWT if available
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // In production, decode JWT to get user ID
            return ip; // For now, use IP
        }
        
        return ip;
    }
    
    // Temporarily disabled - Bucket4j not available
    /*
    private Mono<Bucket> getBucket(String key, Config config) {
        // Use in-memory cache for now, can be enhanced with Redis
        Bucket bucket = cache.computeIfAbsent(key, k -> {
            Refill refill = Refill.intervally(config.getCapacity(), Duration.ofSeconds(config.getRefillPeriod()));
            Bandwidth limit = Bandwidth.classic(config.getCapacity(), refill);
            return Bucket.builder().addLimit(limit).build();
        });
        return Mono.just(bucket);
    }
    */
    
    public static class Config {
        private int capacity = 100;
        private int refillPeriod = 60;
        
        public int getCapacity() {
            return capacity;
        }
        
        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }
        
        public int getRefillPeriod() {
            return refillPeriod;
        }
        
        public void setRefillPeriod(int refillPeriod) {
            this.refillPeriod = refillPeriod;
        }
    }
}

