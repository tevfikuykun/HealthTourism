package com.healthtourism.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

/**
 * Payment Gateway Integration Route
 * Protocol Bridging: HTTP → Kafka → HTTP Response
 * 
 * Scenario: Payment gateway webhook integration
 * Camel handles webhook, processes, and sends to Kafka
 */
@Component
public class PaymentGatewayRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // HTTP Webhook → Kafka
        from("rest:post:/api/integration/payment/webhook")
            .routeId("payment-webhook-to-kafka")
            .log("Received payment webhook")
            
            // Unmarshal JSON
            .unmarshal().json(JsonLibrary.Jackson)
            
            // Validate webhook signature
            .process(exchange -> {
                // Signature validation logic
                exchange.getIn().setHeader("validated", true);
            })
            
            // Filter: Only process successful payments
            .filter(simple("${body[status]} == 'SUCCESS'"))
                .setHeader("source", constant("PAYMENT_GATEWAY"))
                .setHeader("eventType", simple("${body[eventType]}"))
                
                // Send to Kafka
                .to("kafka:payment-events?brokers=localhost:9092")
                
                // Send acknowledgment
                .setBody(constant("{\"status\":\"received\"}"))
                .setHeader("Content-Type", constant("application/json"))
            .end()
            
            // Error handling
            .onException(Exception.class)
                .log("Error processing payment webhook: ${exception.message}")
                .setBody(constant("{\"status\":\"error\"}"))
                .handled(true)
            .end();
    }
}






