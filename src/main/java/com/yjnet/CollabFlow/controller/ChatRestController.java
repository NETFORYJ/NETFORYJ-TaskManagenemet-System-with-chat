package com.yjnet.CollabFlow.controller;

import com.yjnet.CollabFlow.dto.ChatMessageDto;
import com.yjnet.CollabFlow.security.UserDetailsImpl;
import com.yjnet.CollabFlow.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chat")
public class ChatRestController {
    
    @Autowired
    private ChatService chatService;
    
    private UserDetailsImpl getCurrentUser() {
        return (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
    
    @GetMapping("/group/history")
    public ResponseEntity<List<ChatMessageDto>> getGroupChatHistory() {
        UserDetailsImpl currentUser = getCurrentUser();
        List<ChatMessageDto> history = chatService.getGroupChatHistory(currentUser.getTenantId());
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/private/history/{userId}")
    public ResponseEntity<List<ChatMessageDto>> getPrivateChatHistory(@PathVariable Long userId) {
        UserDetailsImpl currentUser = getCurrentUser();
        List<ChatMessageDto> history = chatService.getPrivateChatHistory(currentUser.getId(), userId);
        return ResponseEntity.ok(history);
    }
}