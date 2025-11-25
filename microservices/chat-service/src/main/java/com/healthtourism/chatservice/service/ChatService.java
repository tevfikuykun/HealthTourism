package com.healthtourism.chatservice.service;

import com.healthtourism.chatservice.dto.ChatRoomDTO;
import com.healthtourism.chatservice.dto.MessageDTO;
import com.healthtourism.chatservice.entity.ChatRoom;
import com.healthtourism.chatservice.entity.Message;
import com.healthtourism.chatservice.repository.ChatRoomRepository;
import com.healthtourism.chatservice.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Transactional
    public MessageDTO sendMessage(MessageDTO messageDTO) {
        Message message = new Message();
        message.setSenderId(messageDTO.getSenderId());
        message.setReceiverId(messageDTO.getReceiverId());
        message.setSenderType(messageDTO.getSenderType());
        message.setReceiverType(messageDTO.getReceiverType());
        message.setContent(messageDTO.getContent());
        message.setMessageType(messageDTO.getMessageType() != null ? messageDTO.getMessageType() : "TEXT");
        message.setFileUrl(messageDTO.getFileUrl());
        
        Message saved = messageRepository.save(message);
        
        // Chat room'u güncelle veya oluştur
        ChatRoom chatRoom = chatRoomRepository
            .findChatRoomByParticipants(messageDTO.getSenderId(), messageDTO.getReceiverId())
            .orElseGet(() -> {
                ChatRoom newRoom = new ChatRoom();
                newRoom.setParticipant1Id(messageDTO.getSenderId());
                newRoom.setParticipant2Id(messageDTO.getReceiverId());
                newRoom.setParticipant1Type(messageDTO.getSenderType());
                newRoom.setParticipant2Type(messageDTO.getReceiverType());
                return chatRoomRepository.save(newRoom);
            });
        
        chatRoom.setLastMessageAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
        
        return convertToDTO(saved);
    }
    
    public List<MessageDTO> getConversation(Long userId, Long otherUserId) {
        return messageRepository.findConversation(userId, otherUserId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void markAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Mesaj bulunamadı"));
        
        if (message.getReceiverId().equals(userId)) {
            message.setIsRead(true);
            messageRepository.save(message);
        }
    }
    
    @Transactional
    public void markAllAsRead(Long userId, Long otherUserId) {
        List<Message> unreadMessages = messageRepository
                .findConversation(userId, otherUserId)
                .stream()
                .filter(m -> m.getReceiverId().equals(userId) && !m.getIsRead())
                .collect(Collectors.toList());
        
        unreadMessages.forEach(m -> m.setIsRead(true));
        messageRepository.saveAll(unreadMessages);
    }
    
    public List<ChatRoomDTO> getChatRooms(Long userId) {
        List<ChatRoom> rooms = chatRoomRepository.findChatRoomsByUserId(userId);
        return rooms.stream()
                .map(room -> {
                    ChatRoomDTO dto = convertToDTO(room);
                    Long unreadCount = messageRepository.countUnreadMessages(userId);
                    dto.setUnreadCount(unreadCount);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    public Long getUnreadCount(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }
    
    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setSenderType(message.getSenderType());
        dto.setReceiverType(message.getReceiverType());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setIsRead(message.getIsRead());
        dto.setSentAt(message.getSentAt());
        dto.setFileUrl(message.getFileUrl());
        return dto;
    }
    
    private ChatRoomDTO convertToDTO(ChatRoom room) {
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setId(room.getId());
        dto.setParticipant1Id(room.getParticipant1Id());
        dto.setParticipant2Id(room.getParticipant2Id());
        dto.setParticipant1Type(room.getParticipant1Type());
        dto.setParticipant2Type(room.getParticipant2Type());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setLastMessageAt(room.getLastMessageAt());
        return dto;
    }
}

