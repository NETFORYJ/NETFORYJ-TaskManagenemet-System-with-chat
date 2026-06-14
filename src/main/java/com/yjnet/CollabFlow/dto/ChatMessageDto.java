package com.yjnet.CollabFlow.dto;

import java.time.LocalDateTime;

public class ChatMessageDto {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private String message;
    private Boolean isGroup;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private String type;
    
    // File fields
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String fileType;
    
    public ChatMessageDto() {}
    
    // Getters
    public Long getId() { return id; }
    public Long getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public Long getReceiverId() { return receiverId; }
    public String getReceiverName() { return receiverName; }
    public String getMessage() { return message; }
    public Boolean getIsGroup() { return isGroup; }
    public Boolean getIsRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getType() { return type; }
    public String getFileUrl() { return fileUrl; }
    public String getFileName() { return fileName; }
    public Long getFileSize() { return fileSize; }
    public String getFileType() { return fileType; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public void setMessage(String message) { this.message = message; }
    public void setIsGroup(Boolean isGroup) { this.isGroup = isGroup; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setType(String type) { this.type = type; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setFileType(String fileType) { this.fileType = fileType; }
}