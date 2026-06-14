package com.yjnet.CollabFlow.dto;

import java.time.LocalDate;

public class TaskRequest {
    private String title;
    private String description;
    private String priority;
    private LocalDate dueDate;
    private Long assignTo;
    
    // Constructor
    public TaskRequest() {
    }
    
    // Getters
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public Long getAssignTo() {
        return assignTo;
    }
    
    // Setters
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public void setAssignTo(Long assignTo) {
        this.assignTo = assignTo;
    }
}