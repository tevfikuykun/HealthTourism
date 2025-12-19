package com.healthtourism.camel.tracing;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Camel Tracing Interceptor
 * Adds distributed tracing to Camel routes
 */
@Component
public class CamelTracingInterceptor implements Processor {
    
    @Autowired(required = false)
    private Tracer tracer;
    
    @Override
    public void process(Exchange exchange) throws Exception {
        if (tracer != null) {
            String routeId = exchange.getFromRouteId();
            String endpoint = exchange.getFromEndpoint().getEndpointUri();
            
            // Create span for Camel route
            Span span = tracer.nextSpan()
                .name("camel.route." + routeId)
                .tag("camel.route.id", routeId)
                .tag("camel.endpoint", endpoint)
                .tag("camel.exchange.id", exchange.getExchangeId())
                .start();
            
            try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
                // Process exchange
                exchange.setProperty("tracing.span", span);
                
                // Add trace context to headers
                exchange.getIn().setHeader("X-Trace-Id", span.context().traceId());
                exchange.getIn().setHeader("X-Span-Id", span.context().spanId());
            } catch (Exception e) {
                span.tag("error", true);
                span.tag("error.message", e.getMessage());
                throw e;
            } finally {
                span.end();
            }
        }
    }
}


