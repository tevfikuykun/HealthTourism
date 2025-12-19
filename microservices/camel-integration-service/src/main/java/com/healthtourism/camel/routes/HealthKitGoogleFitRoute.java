package com.healthtourism.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * HealthKit & Google Fit Integration Route
 * 
 * Camel'ın Apple HealthKit ve Google Fit API'lerinden veri çekip
 * IoT Monitoring Service'e beslemesi için route'lar.
 * 
 * Flow:
 * 1. HealthKit/Google Fit API'den veri çek (OAuth2 token ile)
 * 2. Schema Validation (Validator Component ile)
 * 3. Transform to IoT Monitoring format
 * 4. Audit Trail (Immutable logging)
 * 5. Send to IoT Monitoring Service
 */
@Component
public class HealthKitGoogleFitRoute extends RouteBuilder {

    @Value("${healthkit.api.url:https://api.apple.com/healthkit}")
    private String healthKitApiUrl;

    @Value("${googlefit.api.url:https://www.googleapis.com/fitness/v1}")
    private String googleFitApiUrl;

    @Value("${iot.monitoring.service.url:http://localhost:8032}")
    private String iotMonitoringServiceUrl;

    @Value("${audit.service.url:http://localhost:8041}")
    private String auditServiceUrl;

    @Override
    public void configure() throws Exception {

        // ============================================
        // Apple HealthKit Integration Route
        // ============================================
        from("timer:healthkit-poll?period=300000") // Her 5 dakikada bir
            .routeId("healthkit-data-fetch")
            .log("Fetching data from Apple HealthKit")
            
            // OAuth2 Token al (header'da)
            .setHeader("Authorization", simple("Bearer ${header.healthkitToken}"))
            .setHeader("Content-Type", constant("application/json"))
            
            // HealthKit API'den veri çek
            .toD("https4://" + healthKitApiUrl + "/v1/data?bridgeEndpoint=true&throwExceptionOnFailure=false")
            
            // Response'u parse et
            .unmarshal().json(JsonLibrary.Jackson)
            
            // Set source header
            .setHeader("source", constant("HEALTHKIT"))
            
            // Schema Validation (Validator Component)
            .to("direct:validate-health-data")
            
            // Transform HealthKit format → IoT Monitoring format
            .to("direct:transform-healthkit-to-iot")
            
            // Audit Trail (Immutable logging)
            .to("direct:audit-health-data-transfer")
            
            // IoT Monitoring Service'e gönder
            .setHeader("CamelHttpMethod", constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .marshal().json(JsonLibrary.Jackson)
            .toD("http4://" + iotMonitoringServiceUrl + "/api/iot-monitoring/data?bridgeEndpoint=true")
            
            .log("HealthKit data successfully sent to IoT Monitoring Service");

        // ============================================
        // Google Fit Integration Route
        // ============================================
        from("timer:googlefit-poll?period=300000") // Her 5 dakikada bir
            .routeId("googlefit-data-fetch")
            .log("Fetching data from Google Fit")
            
            // OAuth2 Token al (header'da)
            .setHeader("Authorization", simple("Bearer ${header.googlefitToken}"))
            .setHeader("Content-Type", constant("application/json"))
            
            // Google Fit API'den veri çek
            // GET /users/{userId}/dataset:aggregate
            .toD("https4://" + googleFitApiUrl + "/users/me/dataset:aggregate?bridgeEndpoint=true&throwExceptionOnFailure=false")
            
            // Response'u parse et
            .unmarshal().json(JsonLibrary.Jackson)
            
            // Set source header
            .setHeader("source", constant("GOOGLEFIT"))
            
            // Schema Validation (Validator Component)
            .to("direct:validate-health-data")
            
            // Transform Google Fit format → IoT Monitoring format
            .to("direct:transform-googlefit-to-iot")
            
            // Audit Trail (Immutable logging)
            .to("direct:audit-health-data-transfer")
            
            // IoT Monitoring Service'e gönder
            .setHeader("CamelHttpMethod", constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .marshal().json(JsonLibrary.Jackson)
            .toD("http4://" + iotMonitoringServiceUrl + "/api/iot-monitoring/data?bridgeEndpoint=true")
            
            .log("Google Fit data successfully sent to IoT Monitoring Service");

        // ============================================
        // Manual Trigger Route (REST API)
        // ============================================
        from("rest:post:/api/camel/healthkit/fetch")
            .routeId("healthkit-manual-fetch")
            .log("Manual HealthKit data fetch triggered")
            
            // Extract userId from request body
            .unmarshal().json(JsonLibrary.Jackson)
            .setHeader("userId", jsonpath("$.userId"))
            .setHeader("healthkitToken", jsonpath("$.token"))
            
            // Trigger HealthKit fetch
            .to("direct:healthkit-data-fetch")
            
            .marshal().json(JsonLibrary.Jackson)
            .setHeader("Content-Type", constant("application/json"));

        from("rest:post:/api/camel/googlefit/fetch")
            .routeId("googlefit-manual-fetch")
            .log("Manual Google Fit data fetch triggered")
            
            // Extract userId from request body
            .unmarshal().json(JsonLibrary.Jackson)
            .setHeader("userId", jsonpath("$.userId"))
            .setHeader("googlefitToken", jsonpath("$.token"))
            
            // Trigger Google Fit fetch
            .to("direct:googlefit-data-fetch")
            
            .marshal().json(JsonLibrary.Jackson)
            .setHeader("Content-Type", constant("application/json"));
    }
}

