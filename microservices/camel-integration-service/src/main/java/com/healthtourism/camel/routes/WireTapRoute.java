package com.healthtourism.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Wire Tap Route (EIP Pattern)
 * Send copy of message to another channel without affecting main flow
 * 
 * Scenario: Audit logging without blocking main processing
 */
@Component
public class WireTapRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // Payment Processing → Wire Tap for Audit
        from("kafka:payment-processing?brokers=localhost:9092")
            .routeId("payment-processing-wiretap")
            .log("Processing payment")
            
            // Wire Tap (EIP: Wire Tap) - Send copy to audit without blocking
            .wireTap("kafka:audit-payments?brokers=localhost:9092")
            
            // Main processing continues
            .to("kafka:payment-service?brokers=localhost:9092")
            
            .log("Payment processed and audited");
    }
}






