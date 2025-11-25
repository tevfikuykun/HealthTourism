package com.healthtourism.chatservice.controller;

import com.healthtourism.chatservice.dto.ChatRoomDTO;
import com.healthtourism.chatservice.dto.MessageDTO;
import com.healthtourism.chatservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    @Autowired
    private ChatService chatService;
    
    @PostMapping("/message")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO messageDTO) {
        return ResponseEntity.ok(chatService.sendMessage(messageDTO));
    }
    
    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDTO>> getConversation(
            @RequestParam Long userId,
            @RequestParam Long otherUserId) {
        return ResponseEntity.ok(chatService.getConversation(userId, otherUserId));
    }
    
    @GetMapping("/rooms/{userId}")
    public ResponseEntity<List<ChatRoomDTO>> getChatRooms(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getChatRooms(userId));
    }
    
    @GetMapping("/unread/{userId}")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUnreadCount(userId));
    }
    
    @PutMapping("/read/{messageId}")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        chatService.markAsRead(messageId, userId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @RequestParam Long userId,
            @RequestParam Long otherUserId) {
        chatService.markAllAsRead(userId, otherUserId);
        return ResponseEntity.ok().build();
    }
}

