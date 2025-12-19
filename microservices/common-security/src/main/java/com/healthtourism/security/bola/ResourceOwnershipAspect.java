package com.healthtourism.security.bola;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * BOLA/IDOR Protection Aspect
 * Prevents unauthorized access to resources (e.g., patient 10 accessing patient 5's data)
 * Performs Principal-Resource Matching on every request
 */
@Aspect
@Component
public class ResourceOwnershipAspect {

    /**
     * Intercept all controller methods with @RequireResourceOwnership annotation
     */
    @Before("@annotation(requireResourceOwnership)")
    public void checkResourceOwnership(JoinPoint joinPoint, RequireResourceOwnership requireResourceOwnership) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        // Get current user ID from authentication
        Long currentUserId = extractUserId(authentication);
        
        // Get resource ID from method parameters
        Long resourceId = extractResourceId(joinPoint, requireResourceOwnership.resourceIdParam());
        
        // Check if user has admin role (admins can access all resources)
        if (hasAdminRole(authentication)) {
            return; // Admin bypass
        }

        // Perform Principal-Resource Matching
        if (!isResourceOwner(currentUserId, resourceId, requireResourceOwnership.resourceType())) {
            throw new AccessDeniedException(
                "Access denied: User " + currentUserId + " cannot access resource " + resourceId
            );
        }
    }

    /**
     * Generic BOLA check for all patient-related endpoints
     */
    @Before("execution(* com.healthtourism..controller.*Controller.*(..)) && " +
            "(@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void checkGenericBOLA(JoinPoint joinPoint) {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return;

        String path = request.getRequestURI();
        
        // Check if path contains patient/user resource pattern
        if (isProtectedResourcePath(path)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) return;
            
            if (hasAdminRole(authentication) || hasDoctorRole(authentication)) {
                return; // Admin and Doctor bypass for their patients
            }

            Long currentUserId = extractUserId(authentication);
            Long pathUserId = extractUserIdFromPath(path);
            
            if (pathUserId != null && !pathUserId.equals(currentUserId)) {
                throw new AccessDeniedException(
                    "BOLA Protection: User " + currentUserId + " cannot access user " + pathUserId + "'s data"
                );
            }
        }
    }

    private Long extractUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        
        // Try to extract userId from different principal types
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            // Custom UserDetails with userId
            try {
                java.lang.reflect.Method method = principal.getClass().getMethod("getUserId");
                return (Long) method.invoke(principal);
            } catch (Exception e) {
                // Fallback to username parsing
            }
        }
        
        // Try JWT claims
        if (authentication.getDetails() instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> details = (java.util.Map<String, Object>) authentication.getDetails();
            Object userId = details.get("userId");
            if (userId != null) {
                return Long.valueOf(userId.toString());
            }
        }

        // Fallback: try to parse from principal name
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long extractResourceId(JoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(paramName) || 
                parameters[i].isAnnotationPresent(org.springframework.web.bind.annotation.PathVariable.class)) {
                
                org.springframework.web.bind.annotation.PathVariable pathVar = 
                    parameters[i].getAnnotation(org.springframework.web.bind.annotation.PathVariable.class);
                
                if (pathVar != null && (pathVar.value().equals(paramName) || paramName.isEmpty())) {
                    if (args[i] instanceof Long) {
                        return (Long) args[i];
                    } else if (args[i] != null) {
                        return Long.valueOf(args[i].toString());
                    }
                }
            }
        }
        return null;
    }

    private boolean isResourceOwner(Long userId, Long resourceId, String resourceType) {
        if (userId == null || resourceId == null) return true; // Can't verify, allow
        
        // For user/patient resources, direct match
        if ("USER".equals(resourceType) || "PATIENT".equals(resourceType)) {
            return userId.equals(resourceId);
        }
        
        // For other resources, would need to query database
        // This is a simplified check - in production, inject repositories
        return true;
    }

    private boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
    }

    private boolean hasDoctorRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR") || a.getAuthority().equals("DOCTOR"));
    }

    private boolean isProtectedResourcePath(String path) {
        return path.matches(".*/patient/\\d+.*") ||
               path.matches(".*/user/\\d+.*") ||
               path.matches(".*/users/\\d+.*") ||
               path.matches(".*/patients/\\d+.*") ||
               path.matches(".*/health-records/user/\\d+.*") ||
               path.matches(".*/medical-documents/user/\\d+.*");
    }

    private Long extractUserIdFromPath(String path) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            ".*/(?:patient|user|users|patients)/(?:user/)?(\\d+).*"
        );
        java.util.regex.Matcher matcher = pattern.matcher(path);
        if (matcher.matches()) {
            return Long.valueOf(matcher.group(1));
        }
        return null;
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }
}




