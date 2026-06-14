package com.yjnet.CollabFlow.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
public class LoginHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "login_time", updatable = false)
    private LocalDateTime loginTime = LocalDateTime.now();
    
    @Column(name = "logout_time")
    private LocalDateTime logoutTime;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "login_type")
    private LoginType loginType = LoginType.JWT;
    
    public enum LoginType {
        JWT, GOOGLE
    }
    
    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public LocalDateTime getLoginTime() { return loginTime; }
    public LocalDateTime getLogoutTime() { return logoutTime; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getSessionId() { return sessionId; }
    public LoginType getLoginType() { return loginType; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }
    public void setLogoutTime(LocalDateTime logoutTime) { this.logoutTime = logoutTime; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public void setLoginType(LoginType loginType) { this.loginType = loginType; }
}