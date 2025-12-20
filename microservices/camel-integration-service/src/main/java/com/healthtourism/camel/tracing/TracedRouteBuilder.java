package com.healthtourism.camel.tracing;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base RouteBuilder with Tracing Support
 * All Camel routes should extend this for automatic tracing
 */
public abstract class TracedRouteBuilder extends RouteBuilder {
    
    @Autowired(required = false)
    protected Tracer tracer;
    
    /**
     * Create a traced processor that wraps route processing
     */
    protected org.apache.camel.Processor tracedProcessor(String operation, org.apache.camel.Processor processor) {
        return exchange -> {
            if (tracer != null) {
                Span span = tracer.nextSpan()
                    .name("camel." + operation)
                    .tag("camel.route", getRouteId())
                    .start();
                
                try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
                    processor.process(exchange);
                } catch (Exception e) {
                    span.tag("error", true);
                    span.tag("error.message", e.getMessage());
                    throw e;
                } finally {
                    span.end();
                }
            } else {
                processor.process(exchange);
            }
        };
    }
    
    /**
     * Add tracing headers to exchange
     */
    protected void addTracingHeaders(Exchange exchange) {
        if (tracer != null && tracer.currentSpan() != null) {
            Span currentSpan = tracer.currentSpan();
            exchange.getIn().setHeader("X-Trace-Id", currentSpan.context().traceId());
            exchange.getIn().setHeader("X-Span-Id", currentSpan.context().spanId());
        }
    }
}



