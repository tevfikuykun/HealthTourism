package com.healthtourism.camel.config;

import io.micrometer.tracing.Tracer;
import org.apache.camel.CamelContext;
import org.apache.camel.opentelemetry.OpenTelemetryTracer;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Camel Tracing Configuration
 * Enables OpenTelemetry tracing for Camel routes
 */
@Configuration
public class CamelTracingConfig {
    
    @Autowired(required = false)
    private Tracer tracer;
    
    /**
     * Configure Camel Context with OpenTelemetry Tracing
     */
    @Bean
    @ConditionalOnBean(Tracer.class)
    public CamelContextConfiguration camelContextConfiguration() {
        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext camelContext) {
                // Enable OpenTelemetry tracing for Camel
                OpenTelemetryTracer ott = new OpenTelemetryTracer();
                ott.setCamelContext(camelContext);
                ott.init();
                
                // Enable tracing on Camel context
                camelContext.setTracing(true);
            }
            
            @Override
            public void afterApplicationStart(CamelContext camelContext) {
                // Post-initialization if needed
            }
        };
    }
}


