package com.healthtourism.livechatservice.controller;
import com.healthtourism.livechatservice.entity.ChatMessage;
import com.healthtourism.livechatservice.entity.ChatSession;
import com.healthtourism.livechatservice.service.LiveChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/live-chat")
@CrossOrigin(origins = "*")
public class LiveChatController {
    @Autowired
    private LiveChatService service;

    @PostMapping("/session")
    public ResponseEntity<ChatSession> createSession(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) userId = 1L;
        return ResponseEntity.ok(service.createSession(userId));
    }

    @PostMapping("/session/{sessionId}/assign")
    public ResponseEntity<ChatSession> assignAgent(@PathVariable Long sessionId, @RequestBody Map<String, Long> request) {
        return ResponseEntity.ok(service.assignAgent(sessionId, request.get("agentId")));
    }

    @PostMapping("/session/{sessionId}/message")
    public ResponseEntity<ChatMessage> sendMessage(
            @PathVariable Long sessionId,
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) userId = 1L;
        String message = request.get("message").toString();
        String senderType = request.getOrDefault("senderType", "USER").toString();
        return ResponseEntity.ok(service.sendMessage(sessionId, userId, message, senderType));
    }

    @GetMapping("/session/{sessionId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long sessionId) {
        return ResponseEntity.ok(service.getMessages(sessionId));
    }

    @GetMapping("/waiting")
    public ResponseEntity<List<ChatSession>> getWaitingSessions() {
        return ResponseEntity.ok(service.getWaitingSessions());
    }

    @PostMapping("/session/{sessionId}/close")
    public ResponseEntity<Void> closeSession(@PathVariable Long sessionId) {
        service.closeSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}

