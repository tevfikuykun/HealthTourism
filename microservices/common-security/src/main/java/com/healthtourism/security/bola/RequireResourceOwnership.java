package com.healthtourism.security.bola;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that require resource ownership verification
 * Prevents BOLA/IDOR attacks by ensuring users can only access their own resources
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireResourceOwnership {
    
    /**
     * The name of the parameter containing the resource ID
     */
    String resourceIdParam() default "userId";
    
    /**
     * The type of resource being accessed
     */
    String resourceType() default "USER";
    
    /**
     * Allow admin users to bypass the check
     */
    boolean allowAdmin() default true;
    
    /**
     * Allow doctors to access their patients' data
     */
    boolean allowDoctor() default false;
}

