package com.healthtourism.livechatservice.service;
import com.healthtourism.livechatservice.entity.ChatMessage;
import com.healthtourism.livechatservice.entity.ChatSession;
import com.healthtourism.livechatservice.repository.ChatMessageRepository;
import com.healthtourism.livechatservice.repository.ChatSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LiveChatService {
    @Autowired
    private ChatSessionRepository sessionRepository;
    @Autowired
    private ChatMessageRepository messageRepository;

    public ChatSession createSession(Long userId) {
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setStatus("WAITING");
        return sessionRepository.save(session);
    }

    public ChatSession assignAgent(Long sessionId, Long agentId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session bulunamadı"));
        session.setAgentId(agentId);
        session.setStatus("ACTIVE");
        return sessionRepository.save(session);
    }

    public ChatMessage sendMessage(Long sessionId, Long userId, String message, String senderType) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(userId);
        chatMessage.setMessage(message);
        chatMessage.setSenderType(senderType);
        return messageRepository.save(chatMessage);
    }

    public List<ChatMessage> getMessages(Long sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session bulunamadı"));
        return messageRepository.findByUserIdOrderByTimestampAsc(session.getUserId());
    }

    public List<ChatSession> getWaitingSessions() {
        return sessionRepository.findByStatus("WAITING");
    }

    public void closeSession(Long sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session bulunamadı"));
        session.setStatus("CLOSED");
        session.setClosedAt(java.time.LocalDateTime.now());
        sessionRepository.save(session);
    }
}

