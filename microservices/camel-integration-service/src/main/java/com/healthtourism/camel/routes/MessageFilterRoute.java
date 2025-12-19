package com.healthtourism.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

/**
 * Message Filter Route (EIP Pattern)
 * Filter messages based on conditions
 * 
 * Scenario: Filter high-priority notifications
 */
@Component
public class MessageFilterRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // All Notifications → Filter → High Priority
        from("kafka:notifications?brokers=localhost:9092")
            .routeId("notification-filter")
            .log("Received notification")
            
            .unmarshal().json(JsonLibrary.Jackson)
            
            // Filter: Only high priority notifications (EIP: Filter)
            .filter(simple("${body[priority]} == 'HIGH' || ${body[priority]} == 'CRITICAL'"))
                .log("High priority notification: ${body[message]}")
                
                // Route to high-priority queue
                .to("kafka:high-priority-notifications?brokers=localhost:9092")
                
                // Also send to SMS/Email services
                .multicast()
                    .to("direct:sms-service")
                    .to("direct:email-service")
                .end()
            .end()
            
            // Low priority notifications go to regular queue
            .filter(simple("${body[priority]} != 'HIGH' && ${body[priority]} != 'CRITICAL'"))
                .to("kafka:low-priority-notifications?brokers=localhost:9092")
            .end();
    }
}


