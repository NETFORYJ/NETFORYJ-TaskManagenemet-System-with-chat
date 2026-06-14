package com.yjnet.CollabFlow.dto;

import com.yjnet.CollabFlow.entity.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Task.TaskStatus status;
    private Task.Priority priority;
    private Long assignedTo;
    private String assignedToName;
    private Long assignedBy;
    private String assignedByName;
    private Long createdBy;
    private String createdByName;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long takenBy;
    private String takenByName;
    
    private String attachmentUrl;
    private String attachmentName;
    
    
    // Default constructor (REQUIRED for JSON serialization)
    public TaskResponse() {}
    
    // Parameterized constructor
    public TaskResponse(Task task, String assignedToName, String assignedByName, String createdByName) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.priority = task.getPriority();
        this.assignedTo = task.getAssignedTo();
        this.assignedToName = assignedToName;
        this.assignedBy = task.getAssignedBy();
        this.assignedByName = assignedByName;
        this.createdBy = task.getCreatedBy();
        this.createdByName = createdByName;
        this.dueDate = task.getDueDate();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
    }
    
    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Task.TaskStatus getStatus() { return status; }
    public Task.Priority getPriority() { return priority; }
    public Long getAssignedTo() { return assignedTo; }
    public String getAssignedToName() { return assignedToName; }
    public Long getAssignedBy() { return assignedBy; }
    public String getAssignedByName() { return assignedByName; }
    public Long getCreatedBy() { return createdBy; }
    public String getCreatedByName() { return createdByName; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getTakenBy() { return takenBy; }
    public String getTakenByName() { return takenByName; }
    public void setTakenBy(Long takenBy) { this.takenBy = takenBy; }
    public void setTakenByName(String takenByName) { this.takenByName = takenByName; }
    
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    public String getAttachmentName() { return attachmentName; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(Task.TaskStatus status) { this.status = status; }
    public void setPriority(Task.Priority priority) { this.priority = priority; }
    public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }
    public void setAssignedByName(String assignedByName) { this.assignedByName = assignedByName; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}