package com.healthtourism.livechatservice.repository;
import com.healthtourism.livechatservice.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findByUserIdAndStatus(Long userId, String status);
    List<ChatSession> findByStatus(String status);
    List<ChatSession> findByAgentId(Long agentId);
}

