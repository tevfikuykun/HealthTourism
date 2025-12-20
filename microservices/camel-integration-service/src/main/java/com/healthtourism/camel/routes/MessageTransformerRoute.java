package com.healthtourism.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

/**
 * Message Transformer Route (EIP Pattern)
 * Transform message formats
 * 
 * Scenario: Transform JSON to XML for legacy systems
 */
@Component
public class MessageTransformerRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // JSON → XML Transformation
        from("kafka:json-messages?brokers=localhost:9092")
            .routeId("json-to-xml-transformer")
            .log("Transforming JSON to XML")
            
            .unmarshal().json(JsonLibrary.Jackson)
            
            // Transform JSON to XML (EIP: Message Transformer)
            .marshal().jacksonxml()
            
            // Add XML headers
            .setHeader("Content-Type", constant("application/xml"))
            .setHeader("sourceFormat", constant("JSON"))
            .setHeader("targetFormat", constant("XML"))
            
            // Send to XML queue
            .to("kafka:xml-messages?brokers=localhost:9092")
            
            .log("JSON transformed to XML");
        
        // XML → JSON Transformation (Reverse)
        from("kafka:xml-messages-in?brokers=localhost:9092")
            .routeId("xml-to-json-transformer")
            .log("Transforming XML to JSON")
            
            .unmarshal().jacksonxml()
            .marshal().json(JsonLibrary.Jackson)
            
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("sourceFormat", constant("XML"))
            .setHeader("targetFormat", constant("JSON"))
            
            .to("kafka:json-messages-out?brokers=localhost:9092")
            
            .log("XML transformed to JSON");
    }
}



