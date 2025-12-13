package com.healthtourism.common.resilience.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Resilient {
    String circuitBreaker() default "default";
    String retry() default "default";
    String rateLimiter() default "";
    String bulkhead() default "";
    String fallbackMethod() default "";
}

