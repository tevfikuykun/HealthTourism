package com.healthtourism.audit.annotation;

import com.healthtourism.audit.entity.AuditLog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that should be audited
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    AuditLog.ResourceType resourceType();
    AuditLog.Action action();
    String description() default "";
}
