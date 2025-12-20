package com.healthtourism.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.healthtourism.camel.processors.HealthDataTransformer;

/**
 * Health Data Transformer Route
 * 
 * HealthKit ve Google Fit formatlarını IoT Monitoring Service formatına dönüştürür.
 */
@Component
public class HealthDataTransformerRoute extends RouteBuilder {

    @Autowired
    private HealthDataTransformer healthDataTransformer;

    @Override
    public void configure() throws Exception {
        
        // Transform HealthKit to IoT format
        from("direct:transform-healthkit-to-iot")
            .routeId("transform-healthkit-to-iot")
            .log("Transforming HealthKit data to IoT Monitoring format")
            .process(healthDataTransformer)
            .setHeader("deviceType", constant("APPLE_WATCH"))
            .log("HealthKit data transformed successfully");

        // Transform Google Fit to IoT format
        from("direct:transform-googlefit-to-iot")
            .routeId("transform-googlefit-to-iot")
            .log("Transforming Google Fit data to IoT Monitoring format")
            .process(healthDataTransformer)
            .setHeader("deviceType", constant("GOOGLE_FIT"))
            .log("Google Fit data transformed successfully");
    }
}



