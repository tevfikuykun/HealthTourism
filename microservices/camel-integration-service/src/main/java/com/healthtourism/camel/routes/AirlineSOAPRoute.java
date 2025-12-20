package com.healthtourism.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

/**
 * Airline SOAP Integration Route
 * Protocol Bridging: SOAP → REST → Kafka
 * 
 * Scenario: Airline booking system uses SOAP
 * Camel converts SOAP to REST and then to Kafka
 */
@Component
public class AirlineSOAPRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // SOAP → REST → Kafka
        // Alternative: Use direct HTTP endpoint that accepts SOAP
        from("jetty:http://0.0.0.0:8088/airline/soap")
            .routeId("airline-soap-to-kafka")
            .log("Received SOAP request from airline")
            
            // Extract SOAP body
            .process(exchange -> {
                String soapBody = exchange.getIn().getBody(String.class);
                // Extract booking data from SOAP XML
                String json = extractBookingFromSOAP(soapBody);
                exchange.getIn().setBody(json);
            })
            
            // Add headers
            .setHeader("source", constant("AIRLINE_SOAP"))
            .setHeader("contentType", constant("application/json"))
            
            // Send to Kafka
            .to("kafka:flight-bookings?brokers=localhost:9092")
            
            // Return SOAP response
            .setBody(constant("<?xml version=\"1.0\"?><soap:Envelope><soap:Body><response>OK</response></soap:Body></soap:Envelope>"))
            .setHeader("Content-Type", constant("text/xml"))
            
            .log("SOAP request processed and sent to Kafka");
    }
    
    private String extractBookingFromSOAP(String soapBody) {
        // Extract booking data from SOAP XML
        // In production, use JAXB or XPath
        return "{\"bookingId\":\"123\",\"status\":\"confirmed\"}";
    }
}
