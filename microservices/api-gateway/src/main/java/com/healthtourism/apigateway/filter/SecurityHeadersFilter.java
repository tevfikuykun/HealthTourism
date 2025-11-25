package com.healthtourism.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getResponse().getHeaders();
        
        // Security Headers
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("X-XSS-Protection", "1; mode=block");
        headers.add("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        headers.add("Content-Security-Policy", "default-src 'self'");
        headers.add("Referrer-Policy", "strict-origin-when-cross-origin");
        headers.add("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // High priority
    }
}

