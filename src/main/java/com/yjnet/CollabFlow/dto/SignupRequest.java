package com.yjnet.CollabFlow.dto;

public class SignupRequest {
    private String fullName;
    private String email;
    private String password;
    private String role;
    private String tenantName;
    private String subdomain;
    
    // Getters
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getTenantName() { return tenantName; }
    public String getSubdomain() { return subdomain; }
    
    // Setters
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }
    public void setSubdomain(String subdomain) { this.subdomain = subdomain; }
}