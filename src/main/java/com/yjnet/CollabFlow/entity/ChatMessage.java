package com.yjnet.CollabFlow.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    
    @Column(name = "receiver_id")
    private Long receiverId;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "is_group")
    private Boolean isGroup = false;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Column(name = "file_url")
    private String fileUrl;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Getters
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getSenderId() { return senderId; }
    public Long getReceiverId() { return receiverId; }
    public String getMessage() { return message; }
    public Boolean getIsGroup() { return isGroup; }
    public Boolean getIsRead() { return isRead; }
    public String getFileUrl() { return fileUrl; }
    public String getFileName() { return fileName; }
    public Long getFileSize() { return fileSize; }
    public String getFileType() { return fileType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public void setMessage(String message) { this.message = message; }
    public void setIsGroup(Boolean isGroup) { this.isGroup = isGroup; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}