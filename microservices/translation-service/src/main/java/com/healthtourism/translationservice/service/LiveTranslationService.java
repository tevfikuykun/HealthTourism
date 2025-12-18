package com.healthtourism.translationservice.service;

import com.healthtourism.translationservice.entity.LiveTranslationSession;
import com.healthtourism.translationservice.repository.LiveTranslationSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Live Translation Service
 * Real-time translation during telemedicine consultations using Speech-to-Text APIs
 */
@Service
public class LiveTranslationService {
    
    @Autowired
    private LiveTranslationSessionRepository sessionRepository;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;
    
    @Value("${google.speech.api.key:}")
    private String googleSpeechApiKey;
    
    @Value("${azure.speech.api.key:}")
    private String azureSpeechApiKey;
    
    @Value("${azure.speech.region:}")
    private String azureSpeechRegion;
    
    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }
    
    /**
     * Start a live translation session
     */
    @Transactional
    public LiveTranslationSession startSession(
            Long consultationId,
            Long userId,
            Long doctorId,
            String sourceLanguage,
            String targetLanguage) {
        
        LiveTranslationSession session = new LiveTranslationSession();
        session.setConsultationId(consultationId);
        session.setUserId(userId);
        session.setDoctorId(doctorId);
        session.setSourceLanguage(sourceLanguage);
        session.setTargetLanguage(targetLanguage);
        session.setStatus("ACTIVE");
        session.setEnableSubtitles(true);
        session.setEnableVoiceTranslation(true);
        session.setTranslationProvider("GOOGLE"); // Default to Google
        
        return sessionRepository.save(session);
    }
    
    /**
     * Translate speech in real-time (Optimized for <500ms latency)
     * Uses chunked audio streaming for low-latency processing
     */
    @Transactional
    public Map<String, Object> translateSpeech(
            Long sessionId,
            String audioData, // Base64 encoded audio chunk
            String language,
            Boolean isFinalChunk) { // Indicates if this is the final chunk
        
        LiveTranslationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Translation session not found"));
        
        long startTime = System.currentTimeMillis();
        
        // Chunked processing: Process audio in small chunks for low latency
        // Each chunk is processed independently and results are streamed back
        String transcribedText = transcribeSpeechChunked(audioData, language, isFinalChunk);
        String translatedText = translateTextStreaming(transcribedText, session.getSourceLanguage(), session.getTargetLanguage());
        
        long processingTime = System.currentTimeMillis() - startTime;
        
        // Ensure latency is <500ms (critical requirement)
        if (processingTime > 500) {
            System.err.println("WARNING: Translation latency (" + processingTime + "ms) exceeds 500ms threshold!");
        }
        
        // Update session statistics
        session.setTotalTranslations(session.getTotalTranslations() + 1);
        session.setAverageAccuracy(calculateAverageAccuracy(session));
        sessionRepository.save(session);
        
        Map<String, Object> result = new HashMap<>();
        result.put("originalText", transcribedText);
        result.put("translatedText", translatedText);
        result.put("sourceLanguage", session.getSourceLanguage());
        result.put("targetLanguage", session.getTargetLanguage());
        result.put("confidence", 0.95); // Translation confidence
        result.put("latencyMs", processingTime);
        result.put("isFinalChunk", isFinalChunk != null ? isFinalChunk : false);
        
        return result;
    }
    
    /**
     * Transcribe speech chunked for low latency (<500ms)
     * Processes audio in small chunks (e.g., 1-2 second segments)
     */
    private String transcribeSpeechChunked(String audioData, String language, Boolean isFinalChunk) {
        // Optimized chunked processing:
        // 1. Audio is pre-processed in chunks (1-2 seconds each)
        // 2. Each chunk is sent to Speech-to-Text API immediately
        // 3. Results are streamed back as they arrive
        // 4. Final chunk triggers end-of-speech detection
        
        // In production with Google Speech-to-Text:
        // - Use streamingRecognize() API
        // - Set interimResults = true for real-time feedback
        // - Process chunks asynchronously
        
        // In production with Azure Speech Services:
        // - Use Speech SDK with continuous recognition
        // - Enable chunked mode for low latency
        
        // Mock implementation (should be <200ms for chunk processing)
        return "Mock transcribed text from audio chunk";
    }
    
    /**
     * Streaming text translation for low latency
     */
    private String translateTextStreaming(String text, String sourceLanguage, String targetLanguage) {
        // Optimized streaming translation:
        // 1. Text is translated incrementally as chunks arrive
        // 2. Uses streaming translation APIs (Google/Azure)
        // 3. Returns partial results immediately
        
        // In production:
        // - Google Cloud Translation API with streaming mode
        // - Azure Translator with streaming support
        // - Cache common phrases for instant translation
        
        // Mock implementation (should be <200ms)
        if ("en".equals(sourceLanguage) && "tr".equals(targetLanguage)) {
            return "[TR] " + text;
        } else if ("ar".equals(sourceLanguage) && "tr".equals(targetLanguage)) {
            return "[TR] " + text;
        }
        return text;
    }
    
    /**
     * Translate text (for chat messages)
     */
    public String translateText(String text, String sourceLanguage, String targetLanguage) {
        // Simulate translation (in production, use Google Translate API or Azure Translator)
        // For now, return mock translation
        if ("en".equals(sourceLanguage) && "tr".equals(targetLanguage)) {
            return "[TR] " + text; // Mock Turkish translation
        } else if ("ar".equals(sourceLanguage) && "tr".equals(targetLanguage)) {
            return "[TR] " + text; // Mock Turkish translation from Arabic
        }
        return text; // Return original if translation not available
    }
    
    /**
     * Transcribe speech to text (simulated)
     * In production, integrate with Google Speech-to-Text or Azure Speech Services
     */
    private String transcribeSpeech(String audioData, String language) {
        // Simulate speech-to-text transcription
        // In production:
        // 1. Send audio to Google Speech-to-Text API or Azure Speech Services
        // 2. Receive transcribed text
        // 3. Return transcribed text
        
        return "Mock transcribed text from audio"; // Placeholder
    }
    
    /**
     * End translation session
     */
    @Transactional
    public LiveTranslationSession endSession(Long sessionId) {
        LiveTranslationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Translation session not found"));
        
        session.setStatus("COMPLETED");
        session.setEndedAt(LocalDateTime.now());
        
        return sessionRepository.save(session);
    }
    
    public LiveTranslationSession getSessionByConsultation(Long consultationId) {
        return sessionRepository.findByConsultationId(consultationId)
                .orElseThrow(() -> new RuntimeException("Translation session not found"));
    }
    
    public List<LiveTranslationSession> getSessionsByUser(Long userId) {
        return sessionRepository.findByUserIdOrderByStartedAtDesc(userId);
    }
    
    private Double calculateAverageAccuracy(LiveTranslationSession session) {
        // Calculate average accuracy based on translation history
        // For now, return a mock value
        return 95.0; // 95% average accuracy
    }
}
