package com.healthtourism.paymentservice.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * PayPal Payment Gateway Integration Service
 * Supports PayPal REST API v2 for payments
 */
@Service
public class PayPalPaymentService {
    
    @Value("${paypal.client.id:}")
    private String clientId;
    
    @Value("${paypal.client.secret:}")
    private String clientSecret;
    
    @Value("${paypal.mode:sandbox}")
    private String mode; // sandbox or live
    
    private String accessToken;
    private String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @PostConstruct
    public void init() {
        baseUrl = "sandbox".equals(mode) 
            ? "https://api.sandbox.paypal.com" 
            : "https://api.paypal.com";
        
        if (!clientId.isEmpty() && !clientSecret.isEmpty()) {
            refreshAccessToken();
        }
    }
    
    /**
     * Get PayPal Access Token
     */
    private void refreshAccessToken() {
        String tokenUrl = baseUrl + "/v1/oauth2/token";
        
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                accessToken = (String) response.getBody().get("access_token");
            }
        } catch (Exception e) {
            System.err.println("Failed to get PayPal access token: " + e.getMessage());
        }
    }
    
    /**
     * Create PayPal Order
     */
    public Map<String, Object> createOrder(BigDecimal amount, String currency, String description) {
        if (clientId.isEmpty() || clientSecret.isEmpty()) {
            return createSimulatedOrder(amount, currency);
        }
        
        if (accessToken == null) {
            refreshAccessToken();
        }
        
        String orderUrl = baseUrl + "/v2/checkout/orders";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.set("PayPal-Request-Id", java.util.UUID.randomUUID().toString());
        
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("intent", "CAPTURE");
        
        Map<String, Object> purchaseUnit = new HashMap<>();
        purchaseUnit.put("description", description);
        
        Map<String, Object> amountMap = new HashMap<>();
        amountMap.put("currency_code", currency.toUpperCase());
        amountMap.put("value", amount.toString());
        
        Map<String, Object> item = new HashMap<>();
        item.put("name", description);
        item.put("description", description);
        item.put("quantity", "1");
        item.put("unit_amount", amountMap);
        
        purchaseUnit.put("items", new Object[]{item});
        purchaseUnit.put("amount", amountMap);
        
        orderRequest.put("purchase_units", new Object[]{purchaseUnit});
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderRequest, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(orderUrl, request, Map.class);
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                Map<String, Object> order = (Map<String, Object>) response.getBody();
                Map<String, Object> result = new HashMap<>();
                result.put("orderId", order.get("id"));
                
                // Get approval URL from links
                if (order.containsKey("links")) {
                    java.util.List<Map<String, Object>> links = (java.util.List<Map<String, Object>>) order.get("links");
                    for (Map<String, Object> link : links) {
                        if ("approve".equals(link.get("rel"))) {
                            result.put("approvalUrl", link.get("href"));
                            break;
                        }
                    }
                }
                
                result.put("status", order.get("status"));
                return result;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create PayPal order: " + e.getMessage(), e);
        }
        
        throw new RuntimeException("Failed to create PayPal order");
    }
    
    /**
     * Capture PayPal Order
     */
    public Map<String, Object> captureOrder(String orderId) {
        if (clientId.isEmpty() || clientSecret.isEmpty()) {
            return createSimulatedCapture(orderId);
        }
        
        if (accessToken == null) {
            refreshAccessToken();
        }
        
        String captureUrl = baseUrl + "/v2/checkout/orders/" + orderId + "/capture";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(captureUrl, request, Map.class);
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                Map<String, Object> order = (Map<String, Object>) response.getBody();
                Map<String, Object> result = new HashMap<>();
                result.put("orderId", order.get("id"));
                result.put("status", order.get("status"));
                
                // Get transaction ID from purchase units
                if (order.containsKey("purchase_units")) {
                    java.util.List<Map<String, Object>> purchaseUnits = 
                        (java.util.List<Map<String, Object>>) order.get("purchase_units");
                    if (!purchaseUnits.isEmpty()) {
                        Map<String, Object> purchaseUnit = purchaseUnits.get(0);
                        if (purchaseUnit.containsKey("payments")) {
                            Map<String, Object> payments = (Map<String, Object>) purchaseUnit.get("payments");
                            if (payments.containsKey("captures")) {
                                java.util.List<Map<String, Object>> captures = 
                                    (java.util.List<Map<String, Object>>) payments.get("captures");
                                if (!captures.isEmpty()) {
                                    result.put("transactionId", captures.get(0).get("id"));
                                }
                            }
                        }
                    }
                }
                
                return result;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to capture PayPal order: " + e.getMessage(), e);
        }
        
        throw new RuntimeException("Failed to capture PayPal order");
    }
    
    /**
     * Get Order Status
     */
    public Map<String, Object> getOrderStatus(String orderId) {
        if (clientId.isEmpty() || clientSecret.isEmpty()) {
            return Map.of("status", "COMPLETED", "orderId", orderId);
        }
        
        if (accessToken == null) {
            refreshAccessToken();
        }
        
        String orderUrl = baseUrl + "/v2/checkout/orders/" + orderId;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                orderUrl,
                HttpMethod.GET,
                entity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> order = response.getBody();
                Map<String, Object> result = new HashMap<>();
                result.put("orderId", order.get("id"));
                result.put("status", order.get("status"));
                return result;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get PayPal order status: " + e.getMessage(), e);
        }
        
        throw new RuntimeException("Failed to get PayPal order status");
    }
    
    /**
     * Process Refund
     */
    public Map<String, Object> processRefund(String captureId, BigDecimal amount) {
        if (clientId.isEmpty() || clientSecret.isEmpty()) {
            return createSimulatedRefund(captureId);
        }
        
        if (accessToken == null) {
            refreshAccessToken();
        }
        
        String refundUrl = baseUrl + "/v2/payments/captures/" + captureId + "/refund";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        
        Map<String, Object> refundRequest = new HashMap<>();
        if (amount != null) {
            Map<String, Object> amountMap = new HashMap<>();
            amountMap.put("value", amount.toString());
            amountMap.put("currency_code", "USD");
            refundRequest.put("amount", amountMap);
        }
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(refundRequest, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(refundUrl, request, Map.class);
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                Map<String, Object> refund = response.getBody();
                Map<String, Object> result = new HashMap<>();
                result.put("refundId", refund.get("id"));
                result.put("status", refund.get("status"));
                return result;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process PayPal refund: " + e.getMessage(), e);
        }
        
        throw new RuntimeException("Failed to process PayPal refund");
    }
    
    // Simulation methods for testing
    private Map<String, Object> createSimulatedOrder(BigDecimal amount, String currency) {
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", "PAYPAL_SIM_" + System.currentTimeMillis());
        result.put("approvalUrl", "https://www.sandbox.paypal.com/checkoutnow?token=SIMULATED_TOKEN");
        result.put("status", "CREATED");
        return result;
    }
    
    private Map<String, Object> createSimulatedCapture(String orderId) {
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("status", "COMPLETED");
        result.put("transactionId", "PAYPAL_TXN_" + System.currentTimeMillis());
        return result;
    }
    
    private Map<String, Object> createSimulatedRefund(String captureId) {
        Map<String, Object> result = new HashMap<>();
        result.put("refundId", "PAYPAL_REF_" + System.currentTimeMillis());
        result.put("status", "COMPLETED");
        return result;
    }
}

