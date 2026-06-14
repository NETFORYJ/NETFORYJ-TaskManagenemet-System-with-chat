package com.yjnet.CollabFlow.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @Column(nullable = false, length = 50)
    private String action;
    
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;
    
    @Column(name = "entity_id")
    private Long entityId;
    
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;
    
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // ========== GETTERS ==========
    public Long getId() {
        return id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Long getTenantId() {
        return tenantId;
    }
    
    public String getAction() {
        return action;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public Long getEntityId() {
        return entityId;
    }
    
    public String getOldValue() {
        return oldValue;
    }
    
    public String getNewValue() {
        return newValue;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    // ========== SETTERS ==========
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
    
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}