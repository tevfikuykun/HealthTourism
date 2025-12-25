package com.healthtourism.jpa.config.versioning;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API Version Annotation
 * 
 * Marks controllers or endpoints with API version information.
 * Used for API versioning strategy.
 * 
 * Usage:
 * <pre>
 * {@code
 * @ApiVersion("v1")
 * @RestController
 * @RequestMapping("/api/v1/reservations")
 * public class ReservationController {
 *     // ...
 * }
 * }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
    
    /**
     * API version (e.g., "v1", "v2")
     */
    String value();
    
    /**
     * Whether this version is deprecated
     */
    boolean deprecated() default false;
    
    /**
     * Deprecation message (shown in response headers if deprecated)
     */
    String deprecationMessage() default "This API version is deprecated. Please migrate to a newer version.";
}

