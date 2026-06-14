package com.yjnet.CollabFlow.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workspace_files")
public class WorkspaceFile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    
    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "original_name", nullable = false)
    private String originalName;
    
    @Column(name = "file_url", nullable = false)
    private String fileUrl;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "download_count")
    private Integer downloadCount = 0;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Getters
    public Long getId() { return id; }
    public Long getTenantId() { return tenantId; }
    public Long getUploadedBy() { return uploadedBy; }
    public String getFileName() { return fileName; }
    public String getOriginalName() { return originalName; }
    public String getFileUrl() { return fileUrl; }
    public Long getFileSize() { return fileSize; }
    public String getFileType() { return fileType; }
    public String getDescription() { return description; }
    public Integer getDownloadCount() { return downloadCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setDescription(String description) { this.description = description; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}