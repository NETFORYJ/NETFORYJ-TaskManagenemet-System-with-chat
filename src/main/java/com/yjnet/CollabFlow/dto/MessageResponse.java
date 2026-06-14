package com.yjnet.CollabFlow.dto;

public class MessageResponse {
    private String message;
    
    // Constructor
    public MessageResponse(String message) {
        this.message = message;
    }
    
    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}