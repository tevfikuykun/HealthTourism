package com.healthtourism.camel.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Data Audit Trail Route
 * 
 * Her veri geçişinin bir kopyasını (değiştirilemez şekilde) loglar.
 * 
 * Features:
 * - Immutable logging (SHA-256 hash ile)
 * - Blockchain hash gönderimi (opsiyonel)
 * - Audit service entegrasyonu
 * - Timestamp ve metadata
 */
@Component
public class HealthDataAuditTrailRoute extends RouteBuilder {

    @Value("${audit.service.url:http://localhost:8041}")
    private String auditServiceUrl;

    @Value("${blockchain.service.url:http://localhost:8035}")
    private String blockchainServiceUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure() throws Exception {

        // ============================================
        // Health Data Transfer Audit Trail
        // ============================================
        from("direct:audit-health-data-transfer")
            .routeId("audit-health-data-transfer")
            .log("Creating immutable audit trail for health data transfer")
            
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    String body = exchange.getIn().getBody(String.class);
                    String routeId = exchange.getFromRouteId();
                    String source = exchange.getIn().getHeader("source", String.class);
                    String userId = exchange.getIn().getHeader("userId", String.class);
                    
                    // Create audit record
                    Map<String, Object> auditRecord = createAuditRecord(
                        body, routeId, source, userId, exchange
                    );
                    
                    // Calculate SHA-256 hash (immutable proof)
                    String hash = calculateSHA256(body);
                    auditRecord.put("dataHash", hash);
                    
                    // Set audit record as header for downstream processing
                    exchange.getIn().setHeader("auditRecord", objectMapper.writeValueAsString(auditRecord));
                    exchange.getIn().setHeader("dataHash", hash);
                }
            })
            
            // Send to Audit Service (immutable logging)
            .setHeader("CamelHttpMethod", constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .process(exchange -> {
                String auditRecord = exchange.getIn().getHeader("auditRecord", String.class);
                exchange.getIn().setBody(auditRecord);
            })
            .toD("http4://" + auditServiceUrl + "/api/audit/log?bridgeEndpoint=true&throwExceptionOnFailure=false")
            
            // Send hash to Blockchain Service (optional - for extra immutability)
            .choice()
                .when(header("sendToBlockchain").isEqualTo(true))
                    .to("direct:send-hash-to-blockchain")
                .end()
            
            .log("Audit trail created successfully");

        // ============================================
        // Validation Error Audit Trail
        // ============================================
        from("direct:audit-validation-error")
            .routeId("audit-validation-error")
            .log("Creating audit trail for validation error")
            
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    String errorType = exchange.getIn().getHeader("errorType", String.class);
                    String errorMessage = exchange.getIn().getHeader("errorMessage", String.class);
                    String body = exchange.getIn().getBody(String.class);
                    
                    Map<String, Object> auditRecord = new HashMap<>();
                    auditRecord.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    auditRecord.put("eventType", "VALIDATION_ERROR");
                    auditRecord.put("errorType", errorType);
                    auditRecord.put("errorMessage", errorMessage);
                    auditRecord.put("routeId", exchange.getFromRouteId());
                    auditRecord.put("data", body);
                    
                    // Calculate hash
                    String hash = calculateSHA256(body + errorMessage);
                    auditRecord.put("dataHash", hash);
                    
                    exchange.getIn().setBody(objectMapper.writeValueAsString(auditRecord));
                }
            })
            
            .setHeader("CamelHttpMethod", constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .toD("http4://" + auditServiceUrl + "/api/audit/log?bridgeEndpoint=true&throwExceptionOnFailure=false")
            
            .log("Validation error audit trail created");

        // ============================================
        // Blockchain Hash Submission (Optional)
        // ============================================
        from("direct:send-hash-to-blockchain")
            .routeId("send-hash-to-blockchain")
            .log("Sending data hash to blockchain for immutable proof")
            
            .process(exchange -> {
                String hash = exchange.getIn().getHeader("dataHash", String.class);
                String routeId = exchange.getFromRouteId();
                String userId = exchange.getIn().getHeader("userId", String.class);
                
                Map<String, Object> blockchainRecord = new HashMap<>();
                blockchainRecord.put("recordType", "AUDIT_LOG");
                blockchainRecord.put("recordId", "AUDIT-" + System.currentTimeMillis());
                blockchainRecord.put("userId", userId);
                blockchainRecord.put("dataHash", hash);
                blockchainRecord.put("routeId", routeId);
                blockchainRecord.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                
                exchange.getIn().setBody(objectMapper.writeValueAsString(blockchainRecord));
            })
            
            .setHeader("CamelHttpMethod", constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .toD("http4://" + blockchainServiceUrl + "/api/blockchain/create?bridgeEndpoint=true&throwExceptionOnFailure=false")
            
            .log("Hash sent to blockchain successfully");
    }

    /**
     * Create audit record with metadata
     */
    private Map<String, Object> createAuditRecord(
            String data, String routeId, String source, String userId, Exchange exchange) {
        Map<String, Object> record = new HashMap<>();
        record.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        record.put("eventType", "HEALTH_DATA_TRANSFER");
        record.put("routeId", routeId);
        record.put("source", source != null ? source : "UNKNOWN");
        record.put("userId", userId);
        record.put("data", data);
        record.put("dataSize", data.getBytes(StandardCharsets.UTF_8).length);
        record.put("ipAddress", exchange.getIn().getHeader("CamelHttpRemoteAddr", String.class));
        record.put("userAgent", exchange.getIn().getHeader("User-Agent", String.class));
        record.put("validated", exchange.getIn().getHeader("validated", Boolean.class));
        record.put("validationTimestamp", exchange.getIn().getHeader("validationTimestamp", Long.class));
        return record;
    }

    /**
     * Calculate SHA-256 hash for immutable proof
     */
    private String calculateSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate SHA-256 hash", e);
        }
    }
}






