package com.healthtourism.camel.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.ResourceAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenTelemetry Configuration
 * Provides OpenTelemetry SDK for Camel tracing
 */
@Configuration
public class OpenTelemetryConfig {
    
    @Value("${otel.service.name:camel-integration-service}")
    private String serviceName;
    
    @Value("${otel.exporter.zipkin.endpoint:http://localhost:9411/api/v2/spans}")
    private String zipkinEndpoint;
    
    @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault()
            .merge(Resource.create(Attributes.of(
                ResourceAttributes.SERVICE_NAME, serviceName
            )));
        
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(SimpleSpanProcessor.create(
                ZipkinSpanExporter.builder()
                    .setEndpoint(zipkinEndpoint)
                    .build()
            ))
            .setResource(resource)
            .build();
        
        return OpenTelemetrySdk.builder()
            .setTracerProvider(sdkTracerProvider)
            .setPropagators(ContextPropagators.create(
                W3CTraceContextPropagator.getInstance()
            ))
            .buildAndRegisterGlobal();
    }
}



