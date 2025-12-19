package com.healthtourism.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Dead Letter Channel Route (EIP Pattern)
 * Handle failed messages
 * 
 * Scenario: Messages that fail processing go to DLQ
 */
@Component
public class DeadLetterChannelRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // Error handling with Dead Letter Channel
        errorHandler(deadLetterChannel("kafka:dlq?brokers=localhost:9092")
            .maximumRedeliveries(3)
            .redeliveryDelay(1000)
            .retryAttemptedLogLevel(org.apache.camel.LoggingLevel.WARN));
        
        // Example route with error handling
        from("kafka:critical-messages?brokers=localhost:9092")
            .routeId("critical-messages-processor")
            .log("Processing critical message")
            
            // Simulate processing that might fail
            .process(exchange -> {
                // Processing logic
                // If fails, goes to DLQ automatically
            })
            
            .to("kafka:processed-messages?brokers=localhost:9092");
    }
}


