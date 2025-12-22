package com.healthtourism.security.ratelimit;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Payload Size Filter for Buffer Overflow Protection
 * Limits request body size to prevent DoS and buffer overflow attacks
 */
@Component
@Order(1)
public class PayloadSizeFilter implements Filter {

    @Value("${security.payload.max-size-bytes:1048576}") // 1MB default
    private long maxPayloadSize;

    @Value("${security.payload.max-size-upload-bytes:10485760}") // 10MB for uploads
    private long maxUploadSize;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contentLength = httpRequest.getHeader("Content-Length");
        String contentType = httpRequest.getContentType();
        
        if (contentLength != null) {
            long size = Long.parseLong(contentLength);
            long maxAllowed = isUploadRequest(httpRequest, contentType) ? maxUploadSize : maxPayloadSize;
            
            if (size > maxAllowed) {
                httpResponse.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(
                    "{\"error\":\"Payload too large\",\"maxAllowed\":" + maxAllowed + ",\"received\":" + size + "}"
                );
                return;
            }
        }

        // Wrap request to limit reading
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpRequest, maxPayloadSize);
        chain.doFilter(wrappedRequest, response);
    }

    private boolean isUploadRequest(HttpServletRequest request, String contentType) {
        String path = request.getRequestURI();
        return (contentType != null && contentType.startsWith("multipart/")) ||
               path.contains("/upload") ||
               path.contains("/files") ||
               path.contains("/documents");
    }
}








