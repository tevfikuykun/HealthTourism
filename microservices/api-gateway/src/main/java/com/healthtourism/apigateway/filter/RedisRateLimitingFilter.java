package com.healthtourism.apigateway.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
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
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    
    public RedisRateLimitingFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String clientId = getClientId(exchange);
            String key = "rate_limit:" + exchange.getRequest().getURI().getPath() + ":" + clientId;
            
            return getBucket(key, config)
                .flatMap(bucket -> {
                    if (bucket.tryConsume(1)) {
                        return chain.filter(exchange);
                    } else {
                        logger.warn("Rate limit exceeded for client: {} on path: {}", clientId, exchange.getRequest().getURI().getPath());
                        ServerHttpResponse response = exchange.getResponse();
                        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        response.getHeaders().add("Content-Type", "application/json");
                        response.getHeaders().add("X-RateLimit-Limit", String.valueOf(config.getCapacity()));
                        response.getHeaders().add("X-RateLimit-Remaining", "0");
                        response.getHeaders().add("Retry-After", String.valueOf(config.getRefillPeriod()));
                        
                        String message = String.format(
                            "{\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Maximum %d requests per %d seconds.\",\"retryAfter\":%d}",
                            config.getCapacity(), config.getRefillPeriod(), config.getRefillPeriod()
                        );
                        DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
                        return response.writeWith(Mono.just(buffer));
                    }
                });
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
    
    private Mono<Bucket> getBucket(String key, Config config) {
        // Use in-memory cache for now, can be enhanced with Redis
        Bucket bucket = cache.computeIfAbsent(key, k -> {
            Refill refill = Refill.intervally(config.getCapacity(), Duration.ofSeconds(config.getRefillPeriod()));
            Bandwidth limit = Bandwidth.classic(config.getCapacity(), refill);
            return Bucket.builder().addLimit(limit).build();
        });
        return Mono.just(bucket);
    }
    
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

