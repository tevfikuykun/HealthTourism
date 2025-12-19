package com.healthtourism.camel.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Health Data Transformer
 * 
 * HealthKit ve Google Fit formatlarını IoT Monitoring Service formatına dönüştürür.
 */
@Component
public class HealthDataTransformer implements Processor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class);
        String source = exchange.getIn().getHeader("source", String.class);
        String userId = exchange.getIn().getHeader("userId", String.class);
        
        Map<String, Object> iotData;
        
        if ("HEALTHKIT".equals(source)) {
            iotData = transformHealthKitToIoT(body);
        } else if ("GOOGLEFIT".equals(source)) {
            iotData = transformGoogleFitToIoT(body);
        } else {
            throw new IllegalArgumentException("Unknown source: " + source);
        }
        
        // Add required IoT Monitoring Service fields
        if (userId != null) {
            iotData.put("userId", userId);
        }
        iotData.put("deviceType", exchange.getIn().getHeader("deviceType", String.class));
        iotData.put("timestamp", System.currentTimeMillis());
        
        exchange.getIn().setBody(objectMapper.writeValueAsString(iotData));
    }

    /**
     * Transform HealthKit format to IoT Monitoring format
     */
    private Map<String, Object> transformHealthKitToIoT(String healthKitData) {
        JsonNode json = objectMapper.readTree(healthKitData);
        Map<String, Object> iotData = new HashMap<>();
        
        // Extract HealthKit data
        if (json.has("heartRate")) {
            iotData.put("heartRate", json.get("heartRate").asDouble());
        }
        if (json.has("bodyTemperature")) {
            iotData.put("bodyTemperature", json.get("bodyTemperature").asDouble());
        }
        if (json.has("oxygenSaturation")) {
            iotData.put("oxygenSaturation", json.get("oxygenSaturation").asDouble());
        }
        if (json.has("steps")) {
            iotData.put("steps", json.get("steps").asDouble());
        }
        if (json.has("sleepDuration")) {
            iotData.put("sleepDurationHours", json.get("sleepDuration").asDouble() / 3600.0); // seconds to hours
        }
        
        // Set device type
        iotData.put("deviceType", "APPLE_WATCH");
        
        return iotData;
    }

    /**
     * Transform Google Fit format to IoT Monitoring format
     */
    private Map<String, Object> transformGoogleFitToIoT(String googleFitData) {
        JsonNode json = objectMapper.readTree(googleFitData);
        Map<String, Object> iotData = new HashMap<>();
        
        // Extract Google Fit aggregate data
        if (json.has("bucket")) {
            JsonNode bucket = json.get("bucket").get(0);
            if (bucket.has("dataset")) {
                JsonNode dataset = bucket.get("dataset").get(0);
                if (dataset.has("point")) {
                    JsonNode point = dataset.get("point").get(0);
                    if (point.has("value")) {
                        JsonNode value = point.get("value").get(0);
                        
                        // Map Google Fit data types to IoT format
                        String dataTypeName = dataset.get("dataSourceId").asText();
                        if (dataTypeName.contains("heart_rate")) {
                            iotData.put("heartRate", value.get("fpVal").asDouble());
                        } else if (dataTypeName.contains("body_temperature")) {
                            iotData.put("bodyTemperature", value.get("fpVal").asDouble());
                        } else if (dataTypeName.contains("oxygen_saturation")) {
                            iotData.put("oxygenSaturation", value.get("fpVal").asDouble());
                        } else if (dataTypeName.contains("step_count")) {
                            iotData.put("steps", value.get("intVal").asDouble());
                        }
                    }
                }
            }
        }
        
        // Set device type
        iotData.put("deviceType", "GOOGLE_FIT");
        
        return iotData;
    }
}

