package com.yjnet.CollabFlow.controller;

import com.yjnet.CollabFlow.dto.ChatMessageDto;
import com.yjnet.CollabFlow.security.UserDetailsImpl;
import com.yjnet.CollabFlow.service.ChatService;
import com.yjnet.CollabFlow.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chat")
public class ChatFileController {
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    private UserDetailsImpl getCurrentUser() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    @PostMapping("/group/upload")
    public ResponseEntity<?> uploadGroupFile(@RequestParam(value = "file", required = false) MultipartFile file,
                                              @RequestParam(value = "message", required = false) String message) {
        UserDetailsImpl currentUser = getCurrentUser();
        
        if ((file == null || file.isEmpty()) && (message == null || message.trim().isEmpty())) {
            return ResponseEntity.badRequest().body("Please provide a message or file");
        }
        
        if (file != null && !file.isEmpty()) {
            if (!fileStorageService.validateFileSize(file, true)) {
                return ResponseEntity.badRequest().body("File size exceeds 20MB limit");
            }
        }
        
        ChatMessageDto dto = new ChatMessageDto();
        dto.setSenderId(currentUser.getId());
        dto.setSenderName(currentUser.getFullName());
        dto.setMessage(message);
        dto.setIsGroup(true);
        
        if (file != null && !file.isEmpty()) {
            try {
                chatService.sendGroupFileMessage(dto, file, currentUser.getTenantId());
                return ResponseEntity.ok("File sent successfully");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error uploading file: " + e.getMessage());
            }
        } else {
            chatService.sendGroupMessage(dto, currentUser.getTenantId());
            return ResponseEntity.ok("Message sent");
        }
    }
}