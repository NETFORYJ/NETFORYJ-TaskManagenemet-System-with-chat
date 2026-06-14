package com.yjnet.CollabFlow.controller;

import com.yjnet.CollabFlow.dto.ChatMessageDto;
import com.yjnet.CollabFlow.security.UserDetailsImpl;
import com.yjnet.CollabFlow.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
public class ChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    
    @Autowired
    private ChatService chatService;
    
    @MessageMapping("/chat.group.send")
    public void sendGroupMessage(@Payload ChatMessageDto chatMessageDto,
                                  SimpMessageHeaderAccessor headerAccessor,
                                  Principal principal) {
        try {
            Authentication auth = (Authentication) principal;
            UserDetailsImpl currentUser = (UserDetailsImpl) auth.getPrincipal();
            
            chatMessageDto.setSenderId(currentUser.getId());
            chatMessageDto.setSenderName(currentUser.getFullName());
            
            chatService.sendGroupMessage(chatMessageDto, currentUser.getTenantId());
        } catch (Exception e) {
            logger.error("Error sending group message: ", e);
        }
    }
    
    @MessageMapping("/chat.private.send")
    public void sendPrivateMessage(@Payload ChatMessageDto chatMessageDto,
                                    SimpMessageHeaderAccessor headerAccessor,
                                    Principal principal) {
        try {
            Authentication auth = (Authentication) principal;
            UserDetailsImpl currentUser = (UserDetailsImpl) auth.getPrincipal();
            
            chatMessageDto.setSenderId(currentUser.getId());
            chatMessageDto.setSenderName(currentUser.getFullName());
            
            chatService.sendPrivateMessage(chatMessageDto, currentUser.getTenantId());
        } catch (Exception e) {
            logger.error("Error sending private message: ", e);
        }
    }
}