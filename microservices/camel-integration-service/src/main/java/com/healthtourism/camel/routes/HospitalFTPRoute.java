package com.healthtourism.camel.routes;

import com.healthtourism.camel.tracing.TracedRouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

/**
 * Hospital FTP Integration Route
 * Protocol Bridging: FTP → Kafka
 * 
 * Scenario: Hospital sends medical reports via FTP
 * Camel converts them to Kafka messages for internal processing
 * 
 * Tracing: All operations are automatically traced
 */
@Component
public class HospitalFTPRoute extends TracedRouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // FTP → Kafka Bridge
        from("ftp://hospital-ftp-server:21/reports?username=hospital&password=secret&passiveMode=true")
            .routeId("hospital-ftp-to-kafka")
            .log("Received file from hospital FTP: ${header.CamelFileName}")
            
            // Convert file content to JSON
            .unmarshal().json(JsonLibrary.Jackson)
            
            // Add metadata
            .setHeader("source", constant("HOSPITAL_FTP"))
            .setHeader("timestamp", simple("${date:now:yyyy-MM-dd HH:mm:ss}"))
            
            // Transform to internal format (with tracing)
            .process(tracedProcessor("transform-ftp-data", exchange -> {
                // Add processing logic here
                exchange.getIn().setHeader("processed", true);
                addTracingHeaders(exchange);
            }))
            
            // Send to Kafka
            .to("kafka:medical-reports?brokers=localhost:9092")
            
            // Log success
            .log("File processed and sent to Kafka: ${header.CamelFileName}")
            
            // Error handling
            .onException(Exception.class)
                .log("Error processing FTP file: ${exception.message}")
                .to("kafka:error-queue?brokers=localhost:9092")
                .handled(true)
            .end();
    }
}

