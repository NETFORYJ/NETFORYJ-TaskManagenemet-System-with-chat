package com.yjnet.CollabFlow.dto;

public class LoginRequest {
    private String email;
    private String password;
    private Long tenantId;
    
    // Getters
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Long getTenantId() { return tenantId; }
    
    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
}