package com.healthtourism.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter extends AbstractGatewayFilterFactory<RateLimitingFilter.Config> {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    
    public RateLimitingFilter() {
        super(Config.class);
    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String clientId = exchange.getRequest().getRemoteAddress() != null 
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
            
            AtomicInteger count = requestCounts.computeIfAbsent(clientId, k -> new AtomicInteger(0));
            
            if (count.incrementAndGet() > config.getMaxRequests()) {
                logger.warn("Rate limit exceeded for client: {}", clientId);
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                response.getHeaders().add("Content-Type", "application/json");
                
                String message = "{\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Please try again later.\"}";
                DataBuffer buffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
                return response.writeWith(Mono.just(buffer));
            }
            
            // Reset counter after window
            if (count.get() == 1) {
                new Thread(() -> {
                    try {
                        Thread.sleep(config.getWindowSeconds() * 1000L);
                        requestCounts.remove(clientId);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
            
            return chain.filter(exchange);
        };
    }
    
    public static class Config {
        private int maxRequests = 100;
        private int windowSeconds = 60;
        
        public int getMaxRequests() {
            return maxRequests;
        }
        
        public void setMaxRequests(int maxRequests) {
            this.maxRequests = maxRequests;
        }
        
        public int getWindowSeconds() {
            return windowSeconds;
        }
        
        public void setWindowSeconds(int windowSeconds) {
            this.windowSeconds = windowSeconds;
        }
    }
}

