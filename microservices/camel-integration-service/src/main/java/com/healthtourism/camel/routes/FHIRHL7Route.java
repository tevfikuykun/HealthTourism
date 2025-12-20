package com.healthtourism.camel.routes;

import ca.uhn.hl7v2.model.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hl7.HL7MLLPCodec;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

/**
 * FHIR/HL7 Integration Route
 * Format Transformation: HL7 → JSON → Kafka
 * 
 * Scenario: Hospital sends HL7 messages
 * Camel converts HL7 to JSON for internal processing
 */
@Component
public class FHIRHL7Route extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // HL7 MLLP → JSON → Kafka
        from("netty4:tcp://0.0.0.0:8888")
            .routeId("hl7-to-kafka")
            .log("Received HL7 message")
            
            // Unmarshal HL7
            .unmarshal().hl7()
            
            // Convert HL7 to JSON
            .process(exchange -> {
                // Convert HL7 message to JSON structure
                String hl7Message = exchange.getIn().getBody(String.class);
                String json = convertHL7ToJSON(hl7Message);
                exchange.getIn().setBody(json);
            })
            
            // Add metadata
            .setHeader("source", constant("HL7_MLLP"))
            .setHeader("messageType", simple("${body[messageType]}"))
            .setHeader("patientId", simple("${body[patientId]}"))
            
            // Send to Kafka
            .to("kafka:hl7-messages?brokers=localhost:9092")
            
            // Send HL7 ACK response
            .process(exchange -> {
                // Create HL7 ACK message
                String ack = "MSH|^~\\&|ACK|...";
                exchange.getIn().setBody(ack);
            })
            .marshal().hl7()
            
            .log("HL7 message processed and sent to Kafka");
    }
    
    private String convertHL7ToJSON(String hl7Message) {
        // HL7 to JSON conversion logic
        // This would use HAPI library to parse HL7
        // For now, return a simple JSON structure
        return "{\"messageType\":\"ADT\",\"patientId\":\"12345\",\"rawMessage\":\"" + 
               hl7Message.replace("\"", "\\\"") + "\"}";
    }
}

