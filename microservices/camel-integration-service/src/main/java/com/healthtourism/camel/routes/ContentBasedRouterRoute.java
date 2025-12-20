package com.healthtourism.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

/**
 * Content-Based Router Route (EIP Pattern)
 * Route messages to different destinations based on content
 * 
 * Scenario: Route medical documents to appropriate services
 */
@Component
public class ContentBasedRouterRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // Medical Documents → Content-Based Routing
        from("kafka:medical-documents?brokers=localhost:9092")
            .routeId("content-based-router")
            .log("Received medical document")
            
            .unmarshal().json(JsonLibrary.Jackson)
            
            // Content-Based Router (EIP: Content-Based Router)
            .choice()
                .when(simple("${body[documentType]} == 'LAB_RESULT'"))
                    .log("Routing to lab results service")
                    .to("kafka:lab-results?brokers=localhost:9092")
                
                .when(simple("${body[documentType]} == 'RADIOLOGY'"))
                    .log("Routing to radiology service")
                    .to("kafka:radiology-reports?brokers=localhost:9092")
                
                .when(simple("${body[documentType]} == 'PRESCRIPTION'"))
                    .log("Routing to medication service")
                    .to("kafka:prescriptions?brokers=localhost:9092")
                
                .when(simple("${body[documentType]} == 'SURGERY_REPORT'"))
                    .log("Routing to surgery service")
                    .to("kafka:surgery-reports?brokers=localhost:9092")
                
                .otherwise()
                    .log("Routing to general medical documents service")
                    .to("kafka:general-medical-documents?brokers=localhost:9092")
            .end();
    }
}



