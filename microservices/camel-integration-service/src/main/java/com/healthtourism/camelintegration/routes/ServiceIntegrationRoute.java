package com.healthtourism.camelintegration.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Apache Camel Route Builder for Service Integration
 * Handles service-to-service communication, message routing, and transformation
 */
@Component
public class ServiceIntegrationRoute extends RouteBuilder {

    @Value("${service.user.url}")
    private String userServiceUrl;

    @Value("${service.hospital.url}")
    private String hospitalServiceUrl;

    @Value("${service.doctor.url}")
    private String doctorServiceUrl;

    @Value("${service.payment.url}")
    private String paymentServiceUrl;

    @Value("${service.notification.url}")
    private String notificationServiceUrl;

    @Value("${service.reservation.url}")
    private String reservationServiceUrl;

    @Override
    public void configure() throws Exception {
        
        // REST Configuration
        restConfiguration()
            .component("servlet")
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", "true")
            .enableCORS(true)
            .corsHeaderProperty("Access-Control-Allow-Origin", "*")
            .corsHeaderProperty("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // Error Handling
        errorHandler(defaultErrorHandler()
            .maximumRedeliveries(3)
            .redeliveryDelay(1000)
            .retryAttemptedLogLevel(org.apache.camel.LoggingLevel.WARN));

        // Route: Reservation Creation with Payment and Notification
        from("direct:createReservationWithPayment")
            .routeId("reservation-payment-notification")
            .log("Processing reservation with payment: ${body}")
            
            // Step 1: Create Reservation
            .to("direct:createReservation")
            
            // Step 2: Process Payment
            .to("direct:processPayment")
            
            // Step 3: Send Notification
            .to("direct:sendNotification")
            
            // Step 4: Aggregate response
            .log("Reservation created successfully: ${body}");

        // Route: Create Reservation
        from("direct:createReservation")
            .routeId("create-reservation")
            .log("Creating reservation: ${body}")
            .setHeader("CamelHttpMethod", constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .toD("http4://" + reservationServiceUrl + "/api/reservations?bridgeEndpoint=true")
            .log("Reservation created: ${body}");

        // Route: Process Payment
        from("direct:processPayment")
            .routeId("process-payment")
            .log("Processing payment: ${body}")
            .setHeader("CamelHttpMethod", constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .toD("http4://" + paymentServiceUrl + "/api/payments?bridgeEndpoint=true")
            .log("Payment processed: ${body}");

        // Route: Send Notification
        from("direct:sendNotification")
            .routeId("send-notification")
            .log("Sending notification: ${body}")
            .setHeader("CamelHttpMethod", constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .toD("http4://" + notificationServiceUrl + "/api/notifications?bridgeEndpoint=true")
            .log("Notification sent: ${body}");

        // Route: Get User with Hospitals (Aggregation)
        from("direct:getUserWithHospitals")
            .routeId("user-hospitals-aggregation")
            .log("Aggregating user and hospitals data")
            .multicast()
                .parallelProcessing()
                .to("direct:getUser", "direct:getHospitals")
            .end()
            .log("Aggregated data: ${body}");

        // Route: Get User
        from("direct:getUser")
            .routeId("get-user")
            .setHeader("CamelHttpMethod", constant("GET"))
            .toD("http4://" + userServiceUrl + "/api/users/${header.userId}?bridgeEndpoint=true");

        // Route: Get Hospitals
        from("direct:getHospitals")
            .routeId("get-hospitals")
            .setHeader("CamelHttpMethod", constant("GET"))
            .toD("http4://" + hospitalServiceUrl + "/api/hospitals?bridgeEndpoint=true");

        // Route: Message Queue Integration (RabbitMQ)
        from("rabbitmq://reservation-exchange?queue=reservation.queue&routingKey=reservation.created")
            .routeId("rabbitmq-reservation-consumer")
            .log("Received message from RabbitMQ: ${body}")
            .to("direct:processReservationMessage");

        // Route: Process Reservation Message
        from("direct:processReservationMessage")
            .routeId("process-reservation-message")
            .log("Processing reservation message: ${body}")
            .to("direct:sendNotification")
            .log("Reservation message processed");

        // Route: Kafka Integration
        from("kafka:payment-events?brokers=localhost:9092")
            .routeId("kafka-payment-consumer")
            .log("Received payment event from Kafka: ${body}")
            .to("direct:processPaymentEvent");

        // Route: Process Payment Event
        from("direct:processPaymentEvent")
            .routeId("process-payment-event")
            .log("Processing payment event: ${body}")
            .choice()
                .when().simple("${body[status]} == 'COMPLETED'")
                    .to("direct:sendNotification")
                .when().simple("${body[status]} == 'FAILED'")
                    .to("direct:handlePaymentFailure")
            .end();

        // Route: Handle Payment Failure
        from("direct:handlePaymentFailure")
            .routeId("handle-payment-failure")
            .log("Handling payment failure: ${body}")
            .setHeader("notificationType", constant("PAYMENT_FAILED"))
            .to("direct:sendNotification");

        // Route: Data Transformation Example
        from("direct:transformReservationData")
            .routeId("transform-reservation-data")
            .log("Transforming reservation data: ${body}")
            .process(exchange -> {
                // Transform data here
                String body = exchange.getIn().getBody(String.class);
                // Add transformation logic
                exchange.getIn().setBody(body);
            })
            .log("Data transformed: ${body}");

        // Route: Circuit Breaker Pattern
        from("direct:callServiceWithCircuitBreaker")
            .routeId("circuit-breaker-service-call")
            .log("Calling service with circuit breaker: ${body}")
            .circuitBreaker()
                .to("direct:externalServiceCall")
            .onFallback()
                .log("Circuit breaker activated, using fallback")
                .setBody(constant("{\"error\": \"Service unavailable, using fallback\"}"))
            .end();

        // Route: External Service Call
        from("direct:externalServiceCall")
            .routeId("external-service-call")
            .log("Calling external service: ${body}")
            .toD("http4://${header.serviceUrl}?bridgeEndpoint=true")
            .log("External service response: ${body}");
    }
}
