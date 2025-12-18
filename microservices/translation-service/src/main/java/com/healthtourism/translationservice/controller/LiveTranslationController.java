package com.healthtourism.translationservice.controller;

import com.healthtourism.translationservice.entity.LiveTranslationSession;
import com.healthtourism.translationservice.service.LiveTranslationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/translation/live")
@CrossOrigin(origins = "*")
@Tag(name = "Live Translation", description = "Real-time translation during telemedicine consultations")
public class LiveTranslationController {
    
    @Autowired
    private LiveTranslationService liveTranslationService;
    
    @PostMapping("/session/start")
    @Operation(summary = "Start a live translation session",
               description = "Creates a session for real-time translation during telemedicine consultation")
    public ResponseEntity<LiveTranslationSession> startSession(@RequestBody Map<String, Object> request) {
        try {
            Long consultationId = Long.valueOf(request.get("consultationId").toString());
            Long userId = Long.valueOf(request.get("userId").toString());
            Long doctorId = Long.valueOf(request.get("doctorId").toString());
            String sourceLanguage = request.get("sourceLanguage").toString(); // Patient's language
            String targetLanguage = request.get("targetLanguage").toString(); // Doctor's language
            
            LiveTranslationSession session = liveTranslationService.startSession(
                    consultationId, userId, doctorId, sourceLanguage, targetLanguage);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/session/{sessionId}/translate")
    @Operation(summary = "Translate speech in real-time (Chunked for <500ms latency)",
               description = "Translates speech audio chunks to text and then to target language. Optimized for <500ms latency.")
    public ResponseEntity<Map<String, Object>> translateSpeech(
            @PathVariable Long sessionId,
            @RequestBody Map<String, Object> request) {
        try {
            String audioData = request.get("audioData").toString(); // Base64 encoded audio chunk
            String language = request.get("language").toString();
            Boolean isFinalChunk = request.containsKey("isFinalChunk") ? 
                    Boolean.valueOf(request.get("isFinalChunk").toString()) : false;
            
            Map<String, Object> result = liveTranslationService.translateSpeech(
                    sessionId, audioData, language, isFinalChunk);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/translate-text")
    @Operation(summary = "Translate text message",
               description = "Translates text from source language to target language")
    public ResponseEntity<Map<String, Object>> translateText(@RequestBody Map<String, Object> request) {
        try {
            String text = request.get("text").toString();
            String sourceLanguage = request.get("sourceLanguage").toString();
            String targetLanguage = request.get("targetLanguage").toString();
            
            String translatedText = liveTranslationService.translateText(text, sourceLanguage, targetLanguage);
            return ResponseEntity.ok(Map.of(
                    "originalText", text,
                    "translatedText", translatedText,
                    "sourceLanguage", sourceLanguage,
                    "targetLanguage", targetLanguage
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/session/{sessionId}/end")
    @Operation(summary = "End translation session")
    public ResponseEntity<LiveTranslationSession> endSession(@PathVariable Long sessionId) {
        try {
            return ResponseEntity.ok(liveTranslationService.endSession(sessionId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/session/consultation/{consultationId}")
    @Operation(summary = "Get session by consultation ID")
    public ResponseEntity<LiveTranslationSession> getSessionByConsultation(@PathVariable Long consultationId) {
        try {
            return ResponseEntity.ok(liveTranslationService.getSessionByConsultation(consultationId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/session/user/{userId}")
    @Operation(summary = "Get all sessions for a user")
    public ResponseEntity<List<LiveTranslationSession>> getSessionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(liveTranslationService.getSessionsByUser(userId));
    }
}
