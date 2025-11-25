package com.healthtourism.chatservice.repository;

import com.healthtourism.chatservice.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverIdOrderBySentAtAsc(Long senderId, Long receiverId);
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :userId AND m.receiverId = :otherUserId) OR " +
           "(m.senderId = :otherUserId AND m.receiverId = :userId) " +
           "ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);
    
    List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :receiverId AND m.isRead = false")
    Long countUnreadMessages(@Param("receiverId") Long receiverId);
}

