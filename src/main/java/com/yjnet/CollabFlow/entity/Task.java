package com.yjnet.CollabFlow.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.AVAILABLE;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;
    
    @Column(name = "completion_description", columnDefinition = "TEXT")
    private String completionDescription;
    
    @Column(name = "assigned_to")
    private Long assignedTo;
    
    @Column(name = "assigned_by")
    private Long assignedBy;
    
    @Column(name = "created_by", nullable = false)
    private Long createdBy;
    
    @Column(name = "taken_by")
    private Long takenBy;

    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "taken_at")
    private LocalDateTime takenAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "attachment_url")
    private String attachmentUrl;
    @Column(name = "attachment_name")
    private String attachmentName;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public enum TaskStatus {
        AVAILABLE, IN_PROGRESS, COMPLETED, BLOCKED
    }
    
    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // ========== GETTERS ==========
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public Priority getPriority() { return priority; }
    public Long getAssignedTo() { return assignedTo; }
    public Long getAssignedBy() { return assignedBy; }
    public Long getCreatedBy() { return createdBy; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getTakenBy() { return takenBy; }
    public void setTakenBy(Long takenBy) { this.takenBy = takenBy; }
    // ========== SETTERS ==========
    public void setId(Long id) { this.id = id; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
 // Getters and Setters
    public LocalDateTime getTakenAt() { return takenAt; }
    public void setTakenAt(LocalDateTime takenAt) { this.takenAt = takenAt; }
    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
    
    public String getCompletionDescription() { return completionDescription; }
    public void setCompletionDescription(String completionDescription) { this.completionDescription = completionDescription; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    public String getAttachmentName() { return attachmentName; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }
   
}