package com.healthtourism.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;
import org.springframework.stereotype.Component;

/**
 * Aggregator Route (EIP Pattern)
 * Aggregate multiple messages into one
 * 
 * Scenario: Aggregate patient data from multiple sources
 */
@Component
public class AggregatorRoute extends RouteBuilder {
    
    @Override
    public void configure() throws Exception {
        // Multiple Sources → Aggregate → Single Message
        from("kafka:patient-data-fragments?brokers=localhost:9092")
            .routeId("patient-data-aggregator")
            .log("Received patient data fragment")
            
            // Aggregate by patient ID (EIP: Aggregator)
            .aggregate(header("patientId"), new GroupedExchangeAggregationStrategy())
                .completionSize(3) // Wait for 3 fragments
                .completionTimeout(5000) // Or timeout after 5 seconds
                
                .log("Aggregated patient data for: ${header.patientId}")
                
                // Combine all fragments
                .process(exchange -> {
                    // Combine logic here
                    exchange.getIn().setHeader("aggregated", true);
                })
                
                // Send aggregated data
                .to("kafka:complete-patient-data?brokers=localhost:9092")
            .end();
    }
}


