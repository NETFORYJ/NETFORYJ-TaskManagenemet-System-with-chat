package com.yjnet.CollabFlow.controller;

import com.yjnet.CollabFlow.dto.MessageResponse;
import com.yjnet.CollabFlow.dto.TaskRequest;
import com.yjnet.CollabFlow.dto.TaskResponse;
import com.yjnet.CollabFlow.entity.Task;
import com.yjnet.CollabFlow.security.UserDetailsImpl;
import com.yjnet.CollabFlow.service.TaskService;
import com.yjnet.CollabFlow.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return (UserDetailsImpl) authentication.getPrincipal();
        }
        throw new RuntimeException("User not authenticated");
    }
    
    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest request, HttpServletRequest httpRequest) {
        UserDetailsImpl currentUser = getCurrentUser();
        TaskResponse task = taskService.createTask(request, currentUser.getId(), currentUser.getTenantId(), httpRequest);
        return ResponseEntity.ok(task);
    }
    
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        UserDetailsImpl currentUser = getCurrentUser();
        return ResponseEntity.ok(taskService.getAllTasks(currentUser.getTenantId()));
    }
    
    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskResponse>> getMyTasks() {
        UserDetailsImpl currentUser = getCurrentUser();
        return ResponseEntity.ok(taskService.getMyTasks(currentUser.getId(), currentUser.getTenantId()));
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<TaskResponse>> getAvailableTasks() {
        UserDetailsImpl currentUser = getCurrentUser();
        return ResponseEntity.ok(taskService.getAvailableTasks(currentUser.getTenantId()));
    }
    
    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskById(@PathVariable Long taskId) {
        UserDetailsImpl currentUser = getCurrentUser();
        try {
            TaskResponse task = taskService.getTaskById(taskId, currentUser.getTenantId());
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{taskId}/take")
    public ResponseEntity<?> takeTask(@PathVariable Long taskId, HttpServletRequest httpRequest) {
        UserDetailsImpl currentUser = getCurrentUser();
        try {
            TaskResponse task = taskService.takeTask(taskId, currentUser.getId(), currentUser.getTenantId(), httpRequest);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId, @RequestParam Task.TaskStatus status, HttpServletRequest httpRequest) {
        UserDetailsImpl currentUser = getCurrentUser();
        try {
            TaskResponse task = taskService.updateTaskStatus(taskId, status, currentUser.getId(), currentUser.getTenantId(), httpRequest);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PutMapping("/{taskId}/complete")
    public ResponseEntity<?> completeTask(@PathVariable Long taskId, 
                                           @RequestParam("completionDescription") String completionDescription,
                                           @RequestParam(value = "attachment", required = false) MultipartFile attachment,
                                           HttpServletRequest httpRequest) {
        UserDetailsImpl currentUser = getCurrentUser();
        try {
            if (completionDescription == null || completionDescription.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Completion description is required"));
            }
            
            String attachmentUrl = null;
            String attachmentName = null;
            
            if (attachment != null && !attachment.isEmpty()) {
                attachmentUrl = fileStorageService.saveFile(attachment, "task_" + taskId);
                attachmentName = attachment.getOriginalFilename();
            }
            
            TaskResponse task = taskService.completeTask(taskId, completionDescription, attachmentUrl, attachmentName,
                currentUser.getId(), currentUser.getTenantId(), httpRequest);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{taskId}/update")
    public ResponseEntity<?> updateTaskDetails(@PathVariable Long taskId, 
                                                @Valid @RequestBody TaskRequest request,
                                                HttpServletRequest httpRequest) {
        UserDetailsImpl currentUser = getCurrentUser();
        try {
            TaskResponse task = taskService.updateTask(taskId, request, currentUser.getId(), currentUser.getTenantId(), httpRequest);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId, HttpServletRequest httpRequest) {
        UserDetailsImpl currentUser = getCurrentUser();
        taskService.deleteTask(taskId, currentUser.getTenantId(), httpRequest);
        return ResponseEntity.ok(new MessageResponse("Task deleted successfully!"));
    }
    
    @GetMapping("/download/{taskId}")
    public ResponseEntity<?> downloadAttachment(@PathVariable Long taskId) {
        try {
            Task task = taskService.getTaskEntityById(taskId);
            if (task.getAttachmentUrl() == null) {
                return ResponseEntity.notFound().build();
            }
            
            String filePath = System.getProperty("user.dir") + task.getAttachmentUrl();
            java.io.File file = new java.io.File(filePath);
            
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] fileContent = java.nio.file.Files.readAllBytes(file.toPath());
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + task.getAttachmentName() + "\"")
                    .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}