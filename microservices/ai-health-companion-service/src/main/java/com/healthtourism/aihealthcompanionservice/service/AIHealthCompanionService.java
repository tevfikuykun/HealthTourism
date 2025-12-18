package com.healthtourism.aihealthcompanionservice.service;

import com.healthtourism.aihealthcompanionservice.entity.HealthCompanionConversation;
import com.healthtourism.aihealthcompanionservice.repository.HealthCompanionConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;

/**
 * AI Health Companion Service
 * 7/24 Digital Nurse using RAG (Retrieval-Augmented Generation)
 * Analyzes user's medical history and provides contextual health advice
 */
@Service
public class AIHealthCompanionService {
    
    @Autowired
    private HealthCompanionConversationRepository conversationRepository;
    
    @Autowired(required = false)
    private AsyncAIResponseService asyncAIResponseService;
    
    @Autowired(required = false)
    private VectorDatabaseService vectorDatabaseService;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    @Value("${medical.document.service.url:http://localhost:8012}")
    private String medicalDocumentServiceUrl;
    
    @Value("${reservation.service.url:http://localhost:8009}")
    private String reservationServiceUrl;
    
    @Value("${iot.monitoring.service.url:http://localhost:8032}")
    private String iotMonitoringServiceUrl;
    
    @Value("${blockchain.service.url:http://localhost:8030}")
    private String blockchainServiceUrl;
    
    @Value("${patient.risk.scoring.service.url:http://localhost:8036}")
    private String patientRiskScoringServiceUrl;
    
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
    
    /**
     * Ask AI Health Companion a question
     * Uses RAG to retrieve relevant medical context and generate response
     */
    @Transactional
    public HealthCompanionConversation askQuestion(
            Long userId,
            Long reservationId,
            String question) {
        
        // Retrieve user's medical history and context
        Map<String, Object> medicalContext = retrieveMedicalContext(userId, reservationId);
        
        // Retrieve relevant medical knowledge (RAG)
        String retrievedContext = retrieveRelevantKnowledge(question, medicalContext);
        
        // Generate AI response using RAG context
        Map<String, Object> aiResponse = generateAIResponse(question, medicalContext, retrievedContext);
        
        // Create conversation record
        HealthCompanionConversation conversation = new HealthCompanionConversation();
        conversation.setUserId(userId);
        conversation.setReservationId(reservationId);
        conversation.setProcedureId((Long) medicalContext.getOrDefault("procedureId", 0L));
        conversation.setProcedureType((String) medicalContext.getOrDefault("procedureType", "UNKNOWN"));
        conversation.setUserQuestion(question);
        conversation.setAiResponse((String) aiResponse.get("response"));
        conversation.setRetrievedContext(retrievedContext);
        conversation.setMedicalHistoryContext((String) medicalContext.get("summary"));
        conversation.setConfidenceScore((Double) aiResponse.get("confidence"));
        conversation.setResponseType((String) aiResponse.get("responseType"));
        conversation.setUrgencyLevel((String) aiResponse.get("urgencyLevel"));
        conversation.setRequiresDoctorReview((Boolean) aiResponse.get("requiresDoctorReview"));
        conversation.setFollowUpRecommendations((String) aiResponse.get("recommendations"));
        
        return conversationRepository.save(conversation);
    }
    
    /**
     * Retrieve user's medical context
     */
    private Map<String, Object> retrieveMedicalContext(Long userId, Long reservationId) {
        Map<String, Object> context = new HashMap<>();
        
        try {
            // Get reservation details
            String reservationUrl = reservationServiceUrl + "/api/reservations/" + reservationId;
            Map<String, Object> reservation = getRestTemplate().getForObject(reservationUrl, Map.class);
            
            if (reservation != null) {
                context.put("procedureId", reservation.get("doctorId"));
                context.put("procedureType", reservation.getOrDefault("procedureType", "CONSULTATION"));
            }
            
            // Get medical documents
            String medicalDocsUrl = medicalDocumentServiceUrl + "/api/medical-documents/user/" + userId;
            List<Map<String, Object>> medicalDocs = getRestTemplate().getForObject(medicalDocsUrl, List.class);
            
            if (medicalDocs != null && !medicalDocs.isEmpty()) {
                context.put("hasMedicalHistory", true);
                context.put("medicalDocumentCount", medicalDocs.size());
                // Summarize medical history
                context.put("summary", "User has " + medicalDocs.size() + " medical documents on file.");
            } else {
                context.put("hasMedicalHistory", false);
                context.put("summary", "No previous medical history found.");
            }
        } catch (Exception e) {
            System.err.println("Failed to retrieve medical context: " + e.getMessage());
            context.put("summary", "Unable to retrieve medical history.");
        }
        
        return context;
    }
    
    /**
     * Retrieve relevant medical knowledge (RAG) - Enhanced with Vector Database
     * Uses Milvus to search thousands of documents in seconds
     */
    private String retrieveRelevantKnowledge(String question, Map<String, Object> medicalContext) {
        // Use Vector Database if available
        if (vectorDatabaseService != null) {
            try {
                // Generate embedding for question
                List<Float> queryEmbedding = vectorDatabaseService.generateEmbedding(question);
                String embeddingStr = queryEmbedding.toString().replace("[", "").replace("]", "");
                
                // Search top 5 most relevant documents
                List<String> relevantDocs = vectorDatabaseService.searchRelevantDocuments(embeddingStr, 5);
                
                if (!relevantDocs.isEmpty()) {
                    StringBuilder context = new StringBuilder();
                    context.append("Retrieved from medical knowledge base:\n\n");
                    for (int i = 0; i < relevantDocs.size(); i++) {
                        context.append("Document ").append(i + 1).append(": ").append(relevantDocs.get(i)).append("\n\n");
                    }
                    return context.toString();
                }
            } catch (Exception e) {
                System.err.println("Vector database search failed, falling back to keyword search: " + e.getMessage());
            }
        }
        
        // Fallback to original keyword-based search
        return retrieveRelevantKnowledgeFallback(question, medicalContext);
    }
    
    /**
     * Fallback RAG retrieval using keyword matching
     */
    private String retrieveRelevantKnowledgeFallback(String question, Map<String, Object> medicalContext) {
        // Enhanced RAG retrieval with IPFS and IoT context
        // In production:
        // 1. Embed question + IPFS context + IoT data into vector space
        // 2. Search vector database for similar medical knowledge
        // 3. Retrieve top-k relevant documents
        // 4. Return enriched context
        
        String procedureType = (String) medicalContext.getOrDefault("procedureType", "GENERAL");
        String ipfsContext = (String) medicalContext.getOrDefault("ipfsContext", "");
        Map<String, Object> iotContext = (Map<String, Object>) medicalContext.getOrDefault("iotContext", new HashMap<>());
        Map<String, Object> recoveryScore = (Map<String, Object>) medicalContext.getOrDefault("recoveryScore", new HashMap<>());
        
        // Build enriched context for RAG
        StringBuilder enrichedContext = new StringBuilder();
        enrichedContext.append("Procedure Type: ").append(procedureType).append(". ");
        
        // Add IPFS document context
        if (ipfsContext != null && !ipfsContext.isEmpty()) {
            enrichedContext.append("Medical History Context: ").append(ipfsContext).append(" ");
        }
        
        // Add IoT vital signs context
        if (iotContext.containsKey("vitalSignsSummary")) {
            enrichedContext.append("Current Vital Signs: ").append(iotContext.get("vitalSignsSummary")).append(" ");
        }
        
        // Add recovery score context
        if (recoveryScore.containsKey("recoveryScore")) {
            enrichedContext.append("Recovery Score: ").append(recoveryScore.get("recoveryScore"))
                          .append("/100 (").append(recoveryScore.getOrDefault("scoreCategory", "UNKNOWN")).append("). ");
        }
        
        // Mock knowledge base retrieval (enhanced with context)
        Map<String, String> knowledgeBase = new HashMap<>();
        knowledgeBase.put("redness", "Post-operative redness is common in the first 3-7 days. " +
                "If accompanied by fever (>38°C), severe pain (>7/10), or pus discharge, contact your doctor immediately.");
        knowledgeBase.put("pain", "Mild to moderate pain (1-6/10) is normal after surgery. " +
                "Severe pain (>7/10) or pain that increases over time may indicate complications.");
        knowledgeBase.put("swelling", "Swelling typically peaks 48-72 hours after surgery and gradually decreases. " +
                "Apply ice packs and keep the area elevated.");
        knowledgeBase.put("fever", "Low-grade fever (<38°C) can be normal. " +
                "Fever >38.5°C or persistent fever requires immediate medical attention.");
        
        // Enhanced keyword matching with IoT data correlation
        String lowerQuestion = question.toLowerCase();
        String retrievedKnowledge = null;
        
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (lowerQuestion.contains(entry.getKey())) {
                retrievedKnowledge = entry.getValue();
                
                // Enhance with IoT data if available
                if (iotContext.containsKey("latestData")) {
                    Map<String, Object> latestData = (Map<String, Object>) iotContext.get("latestData");
                    if (entry.getKey().equals("pain") && latestData.containsKey("painLevel")) {
                        Double painLevel = Double.valueOf(latestData.get("painLevel").toString());
                        if (painLevel > 7) {
                            retrievedKnowledge += " Your current pain level (" + painLevel + "/10) is elevated. " +
                                                 "Please monitor closely and contact your doctor if it persists.";
                        }
                    }
                    if (entry.getKey().equals("fever") && latestData.containsKey("temperature")) {
                        Double temperature = Double.valueOf(latestData.get("temperature").toString());
                        if (temperature > 38.5) {
                            retrievedKnowledge += " Your current temperature (" + temperature + "°C) is elevated. " +
                                                 "Please seek immediate medical attention.";
                        }
                    }
                }
                break;
            }
        }
        
        if (retrievedKnowledge != null) {
            return enrichedContext.toString() + "\n\n" + retrievedKnowledge;
        }
        
        return enrichedContext.toString() + "\n\nBased on your medical history and procedure type (" + procedureType + "), " +
               "please provide more details about your concern. If symptoms are severe, contact your doctor immediately.";
    }
    
    /**
     * Generate AI response using RAG context
     * In production, use actual LLM (GPT-4, Claude, etc.) with RAG
     */
    private Map<String, Object> generateAIResponse(
            String question,
            Map<String, Object> medicalContext,
            String retrievedContext) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Analyze question urgency
        String urgencyLevel = analyzeUrgency(question);
        String responseType = classifyQuestion(question);
        
        // Generate response based on context
        String aiResponseText = buildResponse(question, medicalContext, retrievedContext, urgencyLevel);
        
        // Determine if doctor review is needed
        boolean requiresDoctorReview = "HIGH".equals(urgencyLevel) || "CRITICAL".equals(urgencyLevel);
        
        response.put("response", aiResponseText);
        response.put("confidence", 0.85); // AI confidence score
        response.put("responseType", responseType);
        response.put("urgencyLevel", urgencyLevel);
        response.put("requiresDoctorReview", requiresDoctorReview);
        response.put("recommendations", generateRecommendations(urgencyLevel, medicalContext));
        
        return response;
    }
    
    private String analyzeUrgency(String question) {
        String lowerQuestion = question.toLowerCase();
        
        if (lowerQuestion.contains("severe") || lowerQuestion.contains("emergency") || 
            lowerQuestion.contains("can't breathe") || lowerQuestion.contains("chest pain")) {
            return "CRITICAL";
        }
        if (lowerQuestion.contains("fever") || lowerQuestion.contains("infection") || 
            lowerQuestion.contains("bleeding") || lowerQuestion.contains("severe pain")) {
            return "HIGH";
        }
        if (lowerQuestion.contains("concern") || lowerQuestion.contains("worry") || 
            lowerQuestion.contains("unusual")) {
            return "MEDIUM";
        }
        return "LOW";
    }
    
    private String classifyQuestion(String question) {
        String lowerQuestion = question.toLowerCase();
        
        if (lowerQuestion.contains("medication") || lowerQuestion.contains("medicine") || 
            lowerQuestion.contains("pill")) {
            return "MEDICATION_ADVICE";
        }
        if (lowerQuestion.contains("symptom") || lowerQuestion.contains("pain") || 
            lowerQuestion.contains("redness") || lowerQuestion.contains("swelling")) {
            return "SYMPTOM_CHECK";
        }
        if (lowerQuestion.contains("normal") || lowerQuestion.contains("expected") || 
            lowerQuestion.contains("should")) {
            return "GENERAL_INFO";
        }
        return "GENERAL_INFO";
    }
    
    private String buildResponse(
            String question,
            Map<String, Object> medicalContext,
            String retrievedContext,
            String urgencyLevel) {
        
        StringBuilder response = new StringBuilder();
        
        // Personalized greeting
        response.append("Based on your medical history and procedure type, ");
        
        // Add retrieved context
        if (retrievedContext != null && !retrievedContext.isEmpty()) {
            response.append(retrievedContext);
        } else {
            response.append("I understand your concern. ");
        }
        
        // Add urgency-specific guidance
        if ("CRITICAL".equals(urgencyLevel)) {
            response.append("\n\n⚠️ URGENT: Please contact your doctor immediately or visit the emergency room.");
        } else if ("HIGH".equals(urgencyLevel)) {
            response.append("\n\n⚠️ Please contact your doctor as soon as possible for evaluation.");
        } else {
            response.append("\n\nIf symptoms persist or worsen, please don't hesitate to contact your healthcare provider.");
        }
        
        return response.toString();
    }
    
    private String generateRecommendations(String urgencyLevel, Map<String, Object> medicalContext) {
        Map<String, Object> recommendations = new HashMap<>();
        
        if ("CRITICAL".equals(urgencyLevel)) {
            recommendations.put("action", "IMMEDIATE_MEDICAL_ATTENTION");
            recommendations.put("message", "Contact emergency services or your doctor immediately");
        } else if ("HIGH".equals(urgencyLevel)) {
            recommendations.put("action", "SCHEDULE_DOCTOR_CONSULTATION");
            recommendations.put("message", "Schedule a consultation with your doctor within 24 hours");
        } else {
            recommendations.put("action", "MONITOR");
            recommendations.put("message", "Continue monitoring symptoms. Contact doctor if condition changes");
        }
        
        return recommendations.toString();
    }
    
    public List<HealthCompanionConversation> getConversationsByUser(Long userId) {
        return conversationRepository.findByUserIdOrderByAskedAtDesc(userId);
    }
    
    public List<HealthCompanionConversation> getConversationsByReservation(Long reservationId) {
        return conversationRepository.findByReservationIdOrderByAskedAtDesc(reservationId);
    }
    
    public HealthCompanionConversation getConversationById(Long id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
    }
    
    /**
     * Async version - processes in background and publishes to Kafka
     */
    public String askQuestionAsync(Long userId, Long reservationId, String question) {
        String requestId = asyncAIResponseService != null ? 
                asyncAIResponseService.generateRequestId() : UUID.randomUUID().toString();
        
        // Process asynchronously
        new Thread(() -> {
            try {
                HealthCompanionConversation conversation = askQuestion(userId, reservationId, question);
                
                // Publish to Kafka
                if (asyncAIResponseService != null) {
                    Map<String, Object> response = Map.of(
                            "conversationId", conversation.getId(),
                            "response", conversation.getAiResponse(),
                            "confidenceScore", conversation.getConfidenceScore(),
                            "urgencyLevel", conversation.getUrgencyLevel()
                    );
                    asyncAIResponseService.publishAICompanionResponse(requestId, userId, response);
                }
            } catch (Exception e) {
                System.err.println("Error processing async AI request: " + e.getMessage());
            }
        }).start();
        
        return requestId;
    }
}
