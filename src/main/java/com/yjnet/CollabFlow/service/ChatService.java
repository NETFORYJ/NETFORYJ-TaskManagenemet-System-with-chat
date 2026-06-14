package com.yjnet.CollabFlow.service;

import com.yjnet.CollabFlow.dto.ChatMessageDto;
import com.yjnet.CollabFlow.entity.ChatMessage;
import com.yjnet.CollabFlow.entity.User;
import com.yjnet.CollabFlow.repository.ChatMessageRepository;
import com.yjnet.CollabFlow.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    // Send and save GROUP message
    @Transactional
    public void sendGroupMessage(ChatMessageDto messageDto, Long tenantId) {
        logger.info("=== SAVING GROUP MESSAGE ===");
        
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setTenantId(tenantId);
        chatMessage.setSenderId(messageDto.getSenderId());
        chatMessage.setMessage(messageDto.getMessage());
        chatMessage.setIsGroup(true);
        chatMessage.setIsRead(false);
        chatMessage.setCreatedAt(LocalDateTime.now());
        
        ChatMessage saved = chatMessageRepository.save(chatMessage);
        
        messageDto.setId(saved.getId());
        messageDto.setCreatedAt(saved.getCreatedAt());
        messageDto.setType("CHAT");
        
        messagingTemplate.convertAndSend("/topic/group/" + tenantId, messageDto);
        logger.info("Group message sent to: /topic/group/{}", tenantId);
    }
    
    // Send and save PRIVATE message
    @Transactional
    public void sendPrivateMessage(ChatMessageDto messageDto, Long tenantId) {
        logger.info("=== SAVING PRIVATE MESSAGE ===");
        
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setTenantId(tenantId);
        chatMessage.setSenderId(messageDto.getSenderId());
        chatMessage.setReceiverId(messageDto.getReceiverId());
        chatMessage.setMessage(messageDto.getMessage());
        chatMessage.setIsGroup(false);
        chatMessage.setIsRead(false);
        chatMessage.setCreatedAt(LocalDateTime.now());
        
        ChatMessage saved = chatMessageRepository.save(chatMessage);
        
        messageDto.setId(saved.getId());
        messageDto.setCreatedAt(saved.getCreatedAt());
        messageDto.setType("CHAT");
        
        messagingTemplate.convertAndSendToUser(
            messageDto.getReceiverId().toString(), 
            "/queue/private", 
            messageDto
        );
        
        messagingTemplate.convertAndSendToUser(
            messageDto.getSenderId().toString(), 
            "/queue/private", 
            messageDto
        );
        logger.info("Private message sent to user: {}", messageDto.getReceiverId());
    }
    
    // Send file in group chat
    @Transactional
    public void sendGroupFileMessage(ChatMessageDto messageDto, MultipartFile file, Long tenantId) {
        try {
            String fileUrl = fileStorageService.saveFile(file, "chat");
            
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setTenantId(tenantId);
            chatMessage.setSenderId(messageDto.getSenderId());
            chatMessage.setMessage(messageDto.getMessage());
            chatMessage.setIsGroup(true);
            chatMessage.setIsRead(false);
            chatMessage.setCreatedAt(LocalDateTime.now());
            chatMessage.setFileUrl(fileUrl);
            chatMessage.setFileName(file.getOriginalFilename());
            chatMessage.setFileSize(file.getSize());
            chatMessage.setFileType(file.getContentType());
            
            chatMessageRepository.save(chatMessage);
            
            messageDto.setFileUrl(fileUrl);
            messageDto.setFileName(file.getOriginalFilename());
            messageDto.setFileSize(file.getSize());
            messageDto.setFileType(file.getContentType());
            messageDto.setType("FILE");
            
            messagingTemplate.convertAndSend("/topic/group/" + tenantId, messageDto);
            logger.info("Group file sent to: /topic/group/{}", tenantId);
        } catch (Exception e) {
            logger.error("Error sending group file: ", e);
        }
    }
    
    // Send file in private chat
    @Transactional
    public void sendPrivateFileMessage(ChatMessageDto messageDto, MultipartFile file, Long tenantId) {
        try {
            String fileUrl = fileStorageService.saveFile(file, "chat");
            
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setTenantId(tenantId);
            chatMessage.setSenderId(messageDto.getSenderId());
            chatMessage.setReceiverId(messageDto.getReceiverId());
            chatMessage.setMessage(messageDto.getMessage());
            chatMessage.setIsGroup(false);
            chatMessage.setIsRead(false);
            chatMessage.setCreatedAt(LocalDateTime.now());
            chatMessage.setFileUrl(fileUrl);
            chatMessage.setFileName(file.getOriginalFilename());
            chatMessage.setFileSize(file.getSize());
            chatMessage.setFileType(file.getContentType());
            
            chatMessageRepository.save(chatMessage);
            
            messageDto.setFileUrl(fileUrl);
            messageDto.setFileName(file.getOriginalFilename());
            messageDto.setFileSize(file.getSize());
            messageDto.setFileType(file.getContentType());
            messageDto.setType("FILE");
            
            messagingTemplate.convertAndSendToUser(
                messageDto.getReceiverId().toString(), 
                "/queue/private", 
                messageDto
            );
            
            messagingTemplate.convertAndSendToUser(
                messageDto.getSenderId().toString(), 
                "/queue/private", 
                messageDto
            );
            logger.info("Private file sent to user: {}", messageDto.getReceiverId());
        } catch (Exception e) {
            logger.error("Error sending private file: ", e);
        }
    }
    
    // Get group chat history
    public List<ChatMessageDto> getGroupChatHistory(Long tenantId) {
        List<ChatMessage> messages = chatMessageRepository.findByTenantIdAndIsGroupTrueOrderByCreatedAtAsc(tenantId);
        return messages.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // Get private chat history
    public List<ChatMessageDto> getPrivateChatHistory(Long user1Id, Long user2Id) {
        List<ChatMessage> messages = chatMessageRepository.findConversation(user1Id, user2Id);
        return messages.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    private ChatMessageDto convertToDto(ChatMessage chatMessage) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(chatMessage.getId());
        dto.setSenderId(chatMessage.getSenderId());
        dto.setReceiverId(chatMessage.getReceiverId());
        dto.setMessage(chatMessage.getMessage());
        dto.setIsGroup(chatMessage.getIsGroup());
        dto.setIsRead(chatMessage.getIsRead());
        dto.setCreatedAt(chatMessage.getCreatedAt());
        dto.setType(chatMessage.getFileUrl() != null ? "FILE" : "CHAT");
        dto.setFileUrl(chatMessage.getFileUrl());
        dto.setFileName(chatMessage.getFileName());
        dto.setFileSize(chatMessage.getFileSize());
        dto.setFileType(chatMessage.getFileType());
        
        userRepository.findById(chatMessage.getSenderId()).ifPresent(user -> {
            dto.setSenderName(user.getFullName());
        });
        
        if (chatMessage.getReceiverId() != null) {
            userRepository.findById(chatMessage.getReceiverId()).ifPresent(user -> {
                dto.setReceiverName(user.getFullName());
            });
        }
        
        return dto;
    }
}