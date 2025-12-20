package com.healthtourism.apikeymanager.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Payload Size Limit Filter for API Key Manager
 * Prevents Buffer Overflow attacks by limiting payload size per API key
 */
@Component
public class PayloadLimitFilter implements Filter {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    private static final String PAYLOAD_KEY_PREFIX = "apikey:payload:";
    private static final long DEFAULT_MAX_PAYLOAD_PER_MINUTE = 10 * 1024 * 1024; // 10MB
    private static final long DEFAULT_MAX_REQUEST_SIZE = 1024 * 1024; // 1MB
    private static final int WINDOW_SECONDS = 60;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Extract API key from header
        String apiKey = httpRequest.getHeader("X-API-Key");
        if (apiKey == null) {
            apiKey = httpRequest.getHeader("Authorization");
        }

        // Get content length
        String contentLengthHeader = httpRequest.getHeader("Content-Length");
        long contentLength = contentLengthHeader != null ? Long.parseLong(contentLengthHeader) : 0;

        // Check single request size limit
        if (contentLength > DEFAULT_MAX_REQUEST_SIZE) {
            sendError(httpResponse, 413, "Request payload too large. Max: " + DEFAULT_MAX_REQUEST_SIZE + " bytes");
            return;
        }

        // Check cumulative payload limit per API key
        if (apiKey != null && redisTemplate != null) {
            String redisKey = PAYLOAD_KEY_PREFIX + sanitizeKey(apiKey);
            
            // Get current total
            Object currentTotalObj = redisTemplate.opsForValue().get(redisKey);
            long currentTotal = currentTotalObj != null ? Long.parseLong(currentTotalObj.toString()) : 0;

            // Check if adding this request would exceed limit
            if (currentTotal + contentLength > DEFAULT_MAX_PAYLOAD_PER_MINUTE) {
                sendError(httpResponse, 429, 
                    "Payload rate limit exceeded. Used: " + currentTotal + 
                    " bytes, Limit: " + DEFAULT_MAX_PAYLOAD_PER_MINUTE + " bytes per minute");
                return;
            }

            // Increment payload counter
            Long newTotal = redisTemplate.opsForValue().increment(redisKey, contentLength);
            if (newTotal != null && newTotal == contentLength) {
                // First request in window, set expiry
                redisTemplate.expire(redisKey, WINDOW_SECONDS, TimeUnit.SECONDS);
            }

            // Add headers for client awareness
            httpResponse.setHeader("X-Payload-Limit", String.valueOf(DEFAULT_MAX_PAYLOAD_PER_MINUTE));
            httpResponse.setHeader("X-Payload-Used", String.valueOf(newTotal));
            httpResponse.setHeader("X-Payload-Remaining", 
                String.valueOf(DEFAULT_MAX_PAYLOAD_PER_MINUTE - newTotal));
        }

        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }

    private String sanitizeKey(String key) {
        // Remove potentially dangerous characters
        return key.replaceAll("[^a-zA-Z0-9-_]", "");
    }
}





