package com.yjnet.CollabFlow.service;

import com.yjnet.CollabFlow.entity.AuditLog;
import com.yjnet.CollabFlow.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public void logAction(Long userId, Long tenantId, String action, String entityType, 
                          Long entityId, String oldValue, String newValue, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(userId);
        auditLog.setTenantId(tenantId);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);
        auditLog.setIpAddress(getClientIp(request));
        auditLog.setCreatedAt(LocalDateTime.now());
        
        auditLogRepository.save(auditLog);
    }
    
    public List<AuditLog> getAuditLogsByTenant(Long tenantId) {
        return auditLogRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }
    
    public List<AuditLog> getAuditLogsByUser(Long userId, Long tenantId) {
        return auditLogRepository.findByUserIdAndTenantIdOrderByCreatedAtDesc(userId, tenantId);
    }
    
    public List<AuditLog> getAuditLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
    
    public List<AuditLog> getRecentAuditLogs(Long tenantId, int limit) {
        List<AuditLog> allLogs = auditLogRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
        return allLogs.stream().limit(limit).collect(java.util.stream.Collectors.toList());
    }
    
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "UNKNOWN";
        }
        
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}