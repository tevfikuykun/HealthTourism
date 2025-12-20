package com.healthtourism.camel.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

/**
 * Health Data Validator Component
 * 
 * Camel üzerinden gelen verinin tıbbi standartlara uygunluğunu
 * "Schema Validation" ile kontrol eder.
 * 
 * Validation Rules:
 * - Heart Rate: 40-220 BPM
 * - Blood Pressure: Systolic 70-250, Diastolic 40-150 mmHg
 * - Body Temperature: 35-42°C
 * - Oxygen Saturation: 70-100%
 * - Steps: >= 0
 * - Sleep Duration: 0-24 hours
 */
@Component
public class HealthDataValidatorRoute extends RouteBuilder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure() throws Exception {

        // ============================================
        // Health Data Validation Route
        // ============================================
        from("direct:validate-health-data")
            .routeId("validate-health-data")
            .log("Validating health data against medical standards")
            
            // Parse JSON
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    String body = exchange.getIn().getBody(String.class);
                    JsonNode jsonNode = objectMapper.readTree(body);
                    
                    ValidationResult result = validateHealthData(jsonNode);
                    
                    if (!result.isValid()) {
                        // Validation failed - send to dead letter channel
                        exchange.getIn().setHeader("validationErrors", result.getErrors());
                        exchange.getIn().setHeader("validationFailed", true);
                        throw new ValidationException("Health data validation failed: " + result.getErrors());
                    }
                    
                    // Validation passed - add validation metadata
                    exchange.getIn().setHeader("validated", true);
                    exchange.getIn().setHeader("validationTimestamp", System.currentTimeMillis());
                    exchange.getIn().setBody(body); // Keep original body
                }
            })
            
            .log("Health data validation passed");

        // ============================================
        // Validation Error Handler (Dead Letter Channel)
        // ============================================
        errorHandler(deadLetterChannel("direct:validation-error-handler")
            .maximumRedeliveries(0)
            .logExhaustedMessageHistory(true));

        from("direct:validation-error-handler")
            .routeId("validation-error-handler")
            .log("Validation error detected: ${exception.message}")
            
            // Log validation errors to audit service
            .setHeader("errorType", constant("VALIDATION_ERROR"))
            .setHeader("errorMessage", simple("${exception.message}"))
            .to("direct:audit-validation-error")
            
            // Send to error queue for manual review
            .to("kafka:health-data-validation-errors?brokers=localhost:9092")
            
            .log("Validation error logged and sent to error queue");
    }

    /**
     * Validate health data against medical standards
     */
    private ValidationResult validateHealthData(JsonNode data) {
        ValidationResult result = new ValidationResult();
        
        // Heart Rate Validation (40-220 BPM)
        if (data.has("heartRate")) {
            double heartRate = data.get("heartRate").asDouble();
            if (heartRate < 40 || heartRate > 220) {
                result.addError("heartRate", "Heart rate must be between 40-220 BPM. Current: " + heartRate);
            }
        }
        
        // Blood Pressure Validation
        if (data.has("bloodPressureSystolic")) {
            double systolic = data.get("bloodPressureSystolic").asDouble();
            if (systolic < 70 || systolic > 250) {
                result.addError("bloodPressureSystolic", 
                    "Systolic blood pressure must be between 70-250 mmHg. Current: " + systolic);
            }
        }
        
        if (data.has("bloodPressureDiastolic")) {
            double diastolic = data.get("bloodPressureDiastolic").asDouble();
            if (diastolic < 40 || diastolic > 150) {
                result.addError("bloodPressureDiastolic", 
                    "Diastolic blood pressure must be between 40-150 mmHg. Current: " + diastolic);
            }
        }
        
        // Body Temperature Validation (35-42°C)
        if (data.has("bodyTemperature")) {
            double temperature = data.get("bodyTemperature").asDouble();
            if (temperature < 35.0 || temperature > 42.0) {
                result.addError("bodyTemperature", 
                    "Body temperature must be between 35-42°C. Current: " + temperature);
            }
        }
        
        // Oxygen Saturation Validation (70-100%)
        if (data.has("oxygenSaturation")) {
            double spo2 = data.get("oxygenSaturation").asDouble();
            if (spo2 < 70 || spo2 > 100) {
                result.addError("oxygenSaturation", 
                    "Oxygen saturation must be between 70-100%. Current: " + spo2);
            }
        }
        
        // Steps Validation (>= 0)
        if (data.has("steps")) {
            double steps = data.get("steps").asDouble();
            if (steps < 0) {
                result.addError("steps", "Steps must be >= 0. Current: " + steps);
            }
        }
        
        // Sleep Duration Validation (0-24 hours)
        if (data.has("sleepDurationHours")) {
            double sleepHours = data.get("sleepDurationHours").asDouble();
            if (sleepHours < 0 || sleepHours > 24) {
                result.addError("sleepDurationHours", 
                    "Sleep duration must be between 0-24 hours. Current: " + sleepHours);
            }
        }
        
        return result;
    }

    /**
     * Validation Result Class
     */
    private static class ValidationResult {
        private boolean valid = true;
        private StringBuilder errors = new StringBuilder();

        public void addError(String field, String message) {
            valid = false;
            if (errors.length() > 0) {
                errors.append("; ");
            }
            errors.append(field).append(": ").append(message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrors() {
            return errors.toString();
        }
    }

    /**
     * Validation Exception
     */
    private static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }
}



