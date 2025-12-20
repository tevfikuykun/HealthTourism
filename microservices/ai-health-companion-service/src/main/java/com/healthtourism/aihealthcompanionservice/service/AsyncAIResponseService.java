package com.healthtourism.aihealthcompanionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Async AI Response Service
 * Publishes AI model results to Kafka/RabbitMQ to avoid gateway timeouts
 */
@Service
public class AsyncAIResponseService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String AI_RESPONSE_TOPIC = "ai-health-companion-responses";
    private static final String RISK_SCORING_TOPIC = "risk-scoring-responses";
    private static final String COST_PREDICTION_TOPIC = "cost-prediction-responses";

    @Autowired
    public AsyncAIResponseService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Publish AI companion response asynchronously
     */
    public void publishAICompanionResponse(String requestId, Long userId, Map<String, Object> response) {
        try {
            Map<String, Object> message = Map.of(
                    "requestId", requestId,
                    "userId", userId,
                    "response", response,
                    "timestamp", System.currentTimeMillis()
            );
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(AI_RESPONSE_TOPIC, requestId, json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish AI response", e);
        }
    }

    /**
     * Publish risk scoring response asynchronously
     */
    public void publishRiskScoringResponse(String requestId, Long userId, Map<String, Object> score) {
        try {
            Map<String, Object> message = Map.of(
                    "requestId", requestId,
                    "userId", userId,
                    "score", score,
                    "timestamp", System.currentTimeMillis()
            );
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(RISK_SCORING_TOPIC, requestId, json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish risk scoring response", e);
        }
    }

    /**
     * Publish cost prediction response asynchronously
     */
    public void publishCostPredictionResponse(String requestId, Long userId, Map<String, Object> prediction) {
        try {
            Map<String, Object> message = Map.of(
                    "requestId", requestId,
                    "userId", userId,
                    "prediction", prediction,
                    "timestamp", System.currentTimeMillis()
            );
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(COST_PREDICTION_TOPIC, requestId, json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish cost prediction response", e);
        }
    }

    /**
     * Generate unique request ID for async tracking
     */
    public String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}





