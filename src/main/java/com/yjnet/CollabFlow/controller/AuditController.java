package com.yjnet.CollabFlow.controller;

import com.yjnet.CollabFlow.entity.AuditLog;
import com.yjnet.CollabFlow.security.UserDetailsImpl;
import com.yjnet.CollabFlow.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/audit")
public class AuditController {
    
    @Autowired
    private AuditService auditService;
    
    private UserDetailsImpl getCurrentUser() {
        return (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
    
    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        UserDetailsImpl currentUser = getCurrentUser();
        return ResponseEntity.ok(auditService.getAuditLogsByTenant(currentUser.getTenantId()));
    }
}