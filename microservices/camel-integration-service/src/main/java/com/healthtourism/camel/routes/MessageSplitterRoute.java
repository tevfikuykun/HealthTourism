package com.healthtourism.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

/**
 * Message Splitter Route (EIP Pattern)
 * Split large messages into smaller chunks
 * 
 * Scenario: Bulk reservation import
 * Split into individual reservation messages
 */
@Component
public class MessageSplitterRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // Bulk Import → Split → Individual Messages
        from("kafka:bulk-reservations?brokers=localhost:9092")
            .routeId("bulk-reservation-splitter")
            .log("Received bulk reservation import")
            
            // Unmarshal JSON
            .unmarshal().json(JsonLibrary.Jackson)
            
            // Split array into individual messages (EIP: Splitter)
            .split().jsonpath("$[*]")
                .log("Processing individual reservation: ${body[id]}")
                
                // Add correlation ID
                .setHeader("correlationId", simple("${body[id]}"))
                .setHeader("batchId", simple("${header.kafka.KEY}"))
                
                // Send to individual processing queue
                .to("kafka:reservations?brokers=localhost:9092")
            .end()
            
            // After all splits, send completion message
            .setBody(constant("{\"status\":\"bulk_import_completed\"}"))
            .to("kafka:bulk-import-status?brokers=localhost:9092")
            
            .log("Bulk reservation import completed");
    }
}






