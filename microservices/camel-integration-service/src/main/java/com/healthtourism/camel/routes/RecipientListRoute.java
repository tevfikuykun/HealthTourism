package com.healthtourism.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Recipient List Route (EIP Pattern)
 * Send message to multiple recipients
 * 
 * Scenario: Reservation confirmation to multiple services
 */
@Component
public class RecipientListRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // Reservation Confirmation → Multiple Recipients
        from("kafka:reservation-confirmed?brokers=localhost:9092")
            .routeId("reservation-confirmation-recipient-list")
            .log("Reservation confirmed, sending to multiple services")
            
            // Recipient List (EIP: Recipient List)
            .recipientList()
                .simple("kafka:email-notifications," +
                       "kafka:sms-notifications," +
                       "kafka:push-notifications," +
                       "kafka:audit-log")
                .delimiter(",")
            .end()
            
            .log("Reservation confirmation sent to all recipients");
    }
}

