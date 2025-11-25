package com.healthtourism.chatservice.repository;

import com.healthtourism.chatservice.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT c FROM ChatRoom c WHERE " +
           "(c.participant1Id = :userId1 AND c.participant2Id = :userId2) OR " +
           "(c.participant1Id = :userId2 AND c.participant2Id = :userId1)")
    Optional<ChatRoom> findChatRoomByParticipants(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    @Query("SELECT c FROM ChatRoom c WHERE c.participant1Id = :userId OR c.participant2Id = :userId ORDER BY c.lastMessageAt DESC")
    List<ChatRoom> findChatRoomsByUserId(@Param("userId") Long userId);
}

