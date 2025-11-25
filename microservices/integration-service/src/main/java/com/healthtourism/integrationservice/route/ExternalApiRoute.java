package com.healthtourism.integrationservice.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExternalApiRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // Flight API Integration Route
        from("direct:flight-search")
            .log("Searching flights: ${body}")
            .to("http://external-flight-api.com/search?bridgeEndpoint=true")
            .log("Flight search result: ${body}");
        
        // Payment Gateway Integration Route
        from("direct:process-payment")
            .log("Processing payment: ${body}")
            .marshal().json()
            .to("http://payment-gateway.com/process?bridgeEndpoint=true")
            .unmarshal().json()
            .log("Payment result: ${body}");
        
        // Email Service Integration Route
        from("direct:send-email")
            .log("Sending email: ${body}")
            .marshal().json()
            .to("http://email-service.com/send?bridgeEndpoint=true")
            .log("Email sent: ${body}");
        
        // SMS Service Integration Route
        from("direct:send-sms")
            .log("Sending SMS: ${body}")
            .marshal().json()
            .to("http://sms-service.com/send?bridgeEndpoint=true")
            .log("SMS sent: ${body}");
        
        // Data Transformation Route
        from("direct:transform-data")
            .log("Transforming data: ${body}")
            .process(exchange -> {
                // Data transformation logic
                String body = exchange.getIn().getBody(String.class);
                // Transform the data
                exchange.getIn().setBody(body);
            })
            .log("Transformed data: ${body}");
        
        // Kafka to HTTP Route
        from("kafka:health-tourism-events?brokers=localhost:9092")
            .log("Received event from Kafka: ${body}")
            .to("direct:process-event");
        
        from("direct:process-event")
            .log("Processing event: ${body}")
            .choice()
                .when().jsonpath("$[?(@.eventType == 'RESERVATION_CREATED')]")
                    .to("direct:handle-reservation-created")
                .when().jsonpath("$[?(@.eventType == 'PAYMENT_COMPLETED')]")
                    .to("direct:handle-payment-completed")
                .otherwise()
                    .to("direct:handle-other-event");
        
        from("direct:handle-reservation-created")
            .log("Handling reservation created event: ${body}")
            .to("direct:send-email");
        
        from("direct:handle-payment-completed")
            .log("Handling payment completed event: ${body}")
            .to("direct:send-email");
        
        from("direct:handle-other-event")
            .log("Handling other event: ${body}");
    }
}


