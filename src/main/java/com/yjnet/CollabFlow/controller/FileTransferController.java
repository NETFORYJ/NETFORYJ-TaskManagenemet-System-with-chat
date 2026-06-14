package com.yjnet.CollabFlow.controller;

import com.yjnet.CollabFlow.dto.MessageResponse;
import com.yjnet.CollabFlow.entity.WorkspaceFile;
import com.yjnet.CollabFlow.repository.UserRepository;
import com.yjnet.CollabFlow.repository.WorkspaceFileRepository;
import com.yjnet.CollabFlow.security.UserDetailsImpl;
import com.yjnet.CollabFlow.service.FileStorageService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/files")
public class FileTransferController {
    
    @Autowired
    private WorkspaceFileRepository workspaceFileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    private UserDetailsImpl getCurrentUser() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "description", required = false) String description) {
        UserDetailsImpl currentUser = getCurrentUser();
        
        try {
            if (!fileStorageService.validateFileSize(file, false)) {
                return ResponseEntity.badRequest().body(new MessageResponse("File size exceeds 300MB limit"));
            }
            
            String fileUrl = fileStorageService.saveFile(file, "transfer");
            
            WorkspaceFile workspaceFile = new WorkspaceFile();
            workspaceFile.setTenantId(currentUser.getTenantId());
            workspaceFile.setUploadedBy(currentUser.getId());
            workspaceFile.setFileName(fileUrl);
            workspaceFile.setOriginalName(file.getOriginalFilename());
            workspaceFile.setFileUrl(fileUrl);
            workspaceFile.setFileSize(file.getSize());
            workspaceFile.setFileType(file.getContentType());
            workspaceFile.setDescription(description);
            
            workspaceFileRepository.save(workspaceFile);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "File uploaded successfully");
            response.put("fileId", workspaceFile.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Upload failed: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllFiles() {
        UserDetailsImpl currentUser = getCurrentUser();
        List<WorkspaceFile> files = workspaceFileRepository.findByTenantIdOrderByCreatedAtDesc(currentUser.getTenantId());
        
        List<Map<String, Object>> response = files.stream().map(file -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", file.getId());
            map.put("fileName", file.getOriginalName());
            map.put("fileSize", formatFileSize(file.getFileSize()));
            map.put("fileType", file.getFileType());
            map.put("description", file.getDescription());
            map.put("downloadCount", file.getDownloadCount());
            map.put("createdAt", file.getCreatedAt());
            map.put("uploadedBy", getUserName(file.getUploadedBy()));
            map.put("uploadedById", file.getUploadedBy());
            map.put("fileUrl", file.getFileUrl());
            return map;
        }).toList();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/download/{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response) {
        try {
            WorkspaceFile file = workspaceFileRepository.findById(id).orElse(null);
            if (file == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            file.setDownloadCount(file.getDownloadCount() + 1);
            workspaceFileRepository.save(file);
            
            File physicalFile = fileStorageService.getFile(file.getFileUrl());
            if (!physicalFile.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            response.setContentType(file.getFileType());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getOriginalName() + "\"");
            
            try (FileInputStream fis = new FileInputStream(physicalFile);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    // ========== DELETE FILE ENDPOINT ==========
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        try {
            UserDetailsImpl currentUser = getCurrentUser();
            WorkspaceFile file = workspaceFileRepository.findById(id).orElse(null);
            
            if (file == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Check if user has permission (same tenant)
            if (!file.getTenantId().equals(currentUser.getTenantId())) {
                return ResponseEntity.status(403).body(new MessageResponse("Unauthorized to delete this file"));
            }
            
            // Delete physical file from storage
            fileStorageService.deleteFile(file.getFileUrl());
            
            // Delete database record
            workspaceFileRepository.deleteById(id);
            
            return ResponseEntity.ok(new MessageResponse("File deleted successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Delete failed: " + e.getMessage()));
        }
    }
    
    private String formatFileSize(Long size) {
        if (size == null) return "0 B";
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.2f MB", size / (1024.0 * 1024));
        return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
    }
    
    private String getUserName(Long userId) {
        return userRepository.findById(userId).map(u -> u.getFullName()).orElse("Unknown");
    }
}