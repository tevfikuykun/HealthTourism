package com.healthtourism.aihealthcompanionservice.controller;

import com.healthtourism.aihealthcompanionservice.entity.HealthCompanionConversation;
import com.healthtourism.aihealthcompanionservice.service.AIHealthCompanionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-health-companion")
@CrossOrigin(origins = "*")
@Tag(name = "AI Health Companion", description = "7/24 Digital Nurse using RAG (Retrieval-Augmented Generation)")
public class AIHealthCompanionController {
    
    @Autowired
    private AIHealthCompanionService aiHealthCompanionService;
    
    @PostMapping("/ask")
    @Operation(summary = "Ask AI Health Companion a question",
               description = "Uses RAG to retrieve medical context and provide personalized health advice")
    public ResponseEntity<HealthCompanionConversation> askQuestion(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long reservationId = Long.valueOf(request.get("reservationId").toString());
            String question = request.get("question").toString();
            
            HealthCompanionConversation conversation = aiHealthCompanionService.askQuestion(
                    userId, reservationId, question);
            
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/ask-async")
    @Operation(summary = "Ask AI Health Companion asynchronously",
               description = "Returns immediately with requestId, response published to Kafka")
    public ResponseEntity<Map<String, String>> askQuestionAsync(@RequestBody Map<String, Object> request) {
        try {
            String requestId = aiHealthCompanionService.askQuestionAsync(
                    Long.valueOf(request.get("userId").toString()),
                    Long.valueOf(request.get("reservationId").toString()),
                    request.get("question").toString());
            
            return ResponseEntity.ok(Map.of(
                    "requestId", requestId,
                    "status", "processing",
                    "message", "Your request is being processed. Results will be published to Kafka topic: ai-health-companion-responses"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all conversations for a user")
    public ResponseEntity<List<HealthCompanionConversation>> getConversationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(aiHealthCompanionService.getConversationsByUser(userId));
    }
    
    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Get all conversations for a reservation")
    public ResponseEntity<List<HealthCompanionConversation>> getConversationsByReservation(
            @PathVariable Long reservationId) {
        return ResponseEntity.ok(aiHealthCompanionService.getConversationsByReservation(reservationId));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get conversation by ID")
    public ResponseEntity<HealthCompanionConversation> getConversationById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(aiHealthCompanionService.getConversationById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
