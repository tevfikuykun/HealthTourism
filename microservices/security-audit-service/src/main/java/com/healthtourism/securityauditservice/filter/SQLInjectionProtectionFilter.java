package com.healthtourism.securityauditservice.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * SQL Injection Protection Filter
 * Blocks SQL injection attempts in request parameters
 */
@Component
@Order(1)
public class SQLInjectionProtectionFilter implements Filter {
    
    // SQL Injection patterns
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
        Pattern.compile("(?i)(.*)(\\b(OR|AND)\\b.*\\b(OR|AND)\\b.*)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)(.*)(\\b(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|EXECUTE)\\b.*)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)(.*)([';]|--|/\\*|\\*/|xp_|sp_)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)(.*)(\\b(UNION|JOIN)\\b.*)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?i)(.*)(\\b(SCRIPT|JAVASCRIPT|ONERROR|ONLOAD)\\b.*)", Pattern.CASE_INSENSITIVE)
    };
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Check all request parameters
        boolean isSQLInjection = false;
        String suspiciousParam = null;
        
        for (String paramName : httpRequest.getParameterMap().keySet()) {
            String[] paramValues = httpRequest.getParameterValues(paramName);
            
            for (String paramValue : paramValues) {
                if (paramValue != null && containsSQLInjection(paramValue)) {
                    isSQLInjection = true;
                    suspiciousParam = paramName + "=" + paramValue;
                    break;
                }
            }
            
            if (isSQLInjection) break;
        }
        
        // Check query string
        String queryString = httpRequest.getQueryString();
        if (!isSQLInjection && queryString != null && containsSQLInjection(queryString)) {
            isSQLInjection = true;
            suspiciousParam = queryString;
        }
        
        if (isSQLInjection) {
            // Log and block the request
            System.err.println("SQL INJECTION ATTEMPT DETECTED: " + suspiciousParam + 
                             " from IP: " + httpRequest.getRemoteAddr());
            
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\":\"SQL Injection attempt detected. Request blocked.\"}");
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    private boolean containsSQLInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(input).find()) {
                return true;
            }
        }
        
        return false;
    }
}
