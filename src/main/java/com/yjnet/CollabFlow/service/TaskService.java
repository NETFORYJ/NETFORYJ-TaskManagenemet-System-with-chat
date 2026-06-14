package com.yjnet.CollabFlow.service;

import com.yjnet.CollabFlow.dto.TaskRequest;
import com.yjnet.CollabFlow.dto.TaskResponse;
import com.yjnet.CollabFlow.entity.*;
import com.yjnet.CollabFlow.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    // Create a new task
    @Transactional
    public TaskResponse createTask(TaskRequest request, Long userId, Long tenantId, HttpServletRequest httpRequest) {
        User createdBy = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Task task = new Task();
        task.setTenantId(tenantId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getPriority() != null) {
            task.setPriority(Task.Priority.valueOf(request.getPriority().toUpperCase()));
        }
        task.setDueDate(request.getDueDate());
        task.setStatus(Task.TaskStatus.AVAILABLE);
        task.setCreatedBy(userId);
        
        if (request.getAssignTo() != null) {
            task.setAssignedTo(request.getAssignTo());
            task.setAssignedBy(userId);
            task.setStatus(Task.TaskStatus.IN_PROGRESS);
            
            // Send notification to assigned user
            createNotification(request.getAssignTo(), tenantId, "Task Assigned", 
                    "You have been assigned: " + request.getTitle(), Notification.NotificationType.TASK_ASSIGNED, null);
        }
        
        Task savedTask = taskRepository.save(task);
        
        // Audit log
        auditService.logAction(userId, tenantId, "CREATE", "Task", savedTask.getId(), 
            null, savedTask.getTitle(), httpRequest);
        
        return buildTaskResponse(savedTask);
    }
    
    // Get all tasks for a tenant
    public List<TaskResponse> getAllTasks(Long tenantId) {
        return taskRepository.findByTenantId(tenantId)
                .stream()
                .map(this::buildTaskResponse)
                .collect(Collectors.toList());
    }
    
    public Task getTaskEntityById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }
    
    
    // Get tasks assigned to a specific user
    public List<TaskResponse> getMyTasks(Long userId, Long tenantId) {
        return taskRepository.findByTenantIdAndAssignedTo(tenantId, userId)
                .stream()
                .map(this::buildTaskResponse)
                .collect(Collectors.toList());
    }
    
    // Get available tasks (not assigned to anyone)
    public List<TaskResponse> getAvailableTasks(Long tenantId) {
        return taskRepository.findByTenantIdAndStatus(tenantId, Task.TaskStatus.AVAILABLE)
                .stream()
                .map(this::buildTaskResponse)
                .collect(Collectors.toList());
    }
    
    // Get single task by ID
    public TaskResponse getTaskById(Long taskId, Long tenantId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Task not found in this workspace");
        }
        
        return buildTaskResponse(task);
    }
    
    // Take a task (assign to yourself)
    @Transactional
    public TaskResponse takeTask(Long taskId, Long userId, Long tenantId, HttpServletRequest httpRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Task not found in this workspace");
        }
        
        if (task.getStatus() != Task.TaskStatus.AVAILABLE) {
            throw new RuntimeException("Task is not available. Current status: " + task.getStatus());
        }
        
        String oldStatus = task.getStatus().name();
        task.setTakenAt(LocalDateTime.now());
        task.setAssignedTo(userId);
        task.setAssignedBy(userId);
        task.setTakenBy(userId);
        task.setStatus(Task.TaskStatus.IN_PROGRESS);
        
        Task updatedTask = taskRepository.save(task);
        
        // Create notification for task creator
        if (task.getCreatedBy() != null && !task.getCreatedBy().equals(userId)) {
            createNotification(task.getCreatedBy(), tenantId, "Task Taken", 
                "User took your task: " + task.getTitle(), Notification.NotificationType.TASK_TAKEN, taskId);
        }
        
        // Broadcast task update via WebSocket
        messagingTemplate.convertAndSend("/topic/tasks/" + tenantId, buildTaskResponse(updatedTask));
        
        // Audit log
        auditService.logAction(userId, tenantId, "TAKE", "Task", task.getId(), 
            oldStatus, task.getStatus().name(), httpRequest);
        
        return buildTaskResponse(updatedTask);
    }
    
    // Update task status
    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, Task.TaskStatus status, Long userId, Long tenantId, HttpServletRequest httpRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Task not found in this workspace");
        }
        
        String oldStatus = task.getStatus().name();
        task.setStatus(status);
        
        if (status == Task.TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
        }
        
        Task updatedTask = taskRepository.save(task);
        
        // Create notification for task assignee if status changed to COMPLETED
        if (status == Task.TaskStatus.COMPLETED && task.getAssignedTo() != null) {
            createNotification(task.getAssignedTo(), tenantId, "Task Completed", 
                "Task completed: " + task.getTitle(), Notification.NotificationType.TASK_COMPLETED, taskId);
        }
        
        // Broadcast task update via WebSocket
        messagingTemplate.convertAndSend("/topic/tasks/" + tenantId, buildTaskResponse(updatedTask));
        
        // Audit log
        auditService.logAction(userId, tenantId, "UPDATE_STATUS", "Task", task.getId(), 
            oldStatus, status.name(), httpRequest);
        
        return buildTaskResponse(updatedTask);
    }
    
    // Complete task with description
 // Complete task with description and attachment info

    @Transactional
    public TaskResponse completeTask(Long taskId, String completionDescription, String attachmentUrl, String attachmentName, 
                                      Long userId, Long tenantId, HttpServletRequest httpRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Task not found in this workspace");
        }
        
        if (task.getAssignedTo() == null || !task.getAssignedTo().equals(userId)) {
            throw new RuntimeException("You are not assigned to this task");
        }
        
        String oldStatus = task.getStatus().name();
        task.setStatus(Task.TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setCompletionDescription(completionDescription);
        
        // Save attachment info
        if (attachmentUrl != null && !attachmentUrl.isEmpty()) {
            task.setAttachmentUrl(attachmentUrl);
            task.setAttachmentName(attachmentName);
            System.out.println("Attachment saved: " + attachmentUrl + " - " + attachmentName);
        }
        
        Task updatedTask = taskRepository.save(task);
        
        // Broadcast task update via WebSocket
        messagingTemplate.convertAndSend("/topic/tasks/" + tenantId, buildTaskResponse(updatedTask));
        
        // Audit log
        auditService.logAction(userId, tenantId, "COMPLETE", "Task", task.getId(), 
            oldStatus, "COMPLETED with note: " + completionDescription + " Attachment: " + attachmentName, httpRequest);
        
        return buildTaskResponse(updatedTask);
    }
    
    
    // Update task details
    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request, Long userId, Long tenantId, HttpServletRequest httpRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Task not found in this workspace");
        }
        
        String oldTitle = task.getTitle();
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getPriority() != null) {
            task.setPriority(Task.Priority.valueOf(request.getPriority().toUpperCase()));
        }
        task.setDueDate(request.getDueDate());
        
        Task updatedTask = taskRepository.save(task);
        
        // Broadcast task update via WebSocket
        messagingTemplate.convertAndSend("/topic/tasks/" + tenantId, buildTaskResponse(updatedTask));
        
        // Audit log
        auditService.logAction(userId, tenantId, "UPDATE", "Task", task.getId(), 
            oldTitle, request.getTitle(), httpRequest);
        
        return buildTaskResponse(updatedTask);
    }
    
    // Delete a task
    @Transactional
    public void deleteTask(Long taskId, Long tenantId, HttpServletRequest httpRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Task not found in this workspace");
        }
        
        String taskTitle = task.getTitle();
        taskRepository.delete(task);
        
        // Broadcast task deletion via WebSocket
        messagingTemplate.convertAndSend("/topic/tasks/" + tenantId, "DELETED:" + taskId);
        
        // Audit log
        auditService.logAction(0L, tenantId, "DELETE", "Task", taskId, 
            taskTitle, "DELETED", httpRequest);
    }
    
    // Get task statistics for dashboard
    public TaskStats getTaskStats(Long tenantId) {
        TaskStats stats = new TaskStats();
        stats.setTotalTasks(taskRepository.findByTenantId(tenantId).size());
        stats.setAvailableTasks((int) taskRepository.countAvailableTasks(tenantId));
        stats.setInProgressTasks((int) taskRepository.countInProgressTasks(tenantId));
        stats.setCompletedTasks((int) taskRepository.findByTenantIdAndStatus(tenantId, Task.TaskStatus.COMPLETED).size());
        return stats;
    }
    
    // Inner class for task statistics
    public static class TaskStats {
        private int totalTasks;
        private int availableTasks;
        private int inProgressTasks;
        private int completedTasks;
        
        public int getTotalTasks() { return totalTasks; }
        public int getAvailableTasks() { return availableTasks; }
        public int getInProgressTasks() { return inProgressTasks; }
        public int getCompletedTasks() { return completedTasks; }
        
        public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }
        public void setAvailableTasks(int availableTasks) { this.availableTasks = availableTasks; }
        public void setInProgressTasks(int inProgressTasks) { this.inProgressTasks = inProgressTasks; }
        public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }
    }
    
    private void createNotification(Long userId, Long tenantId, String title, String message, 
                                     Notification.NotificationType type, Long taskId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTenantId(tenantId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedEntityId(taskId);
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }
    
    
    private TaskResponse buildTaskResponse(Task task) {
        String assignedToName = "";
        if (task.getAssignedTo() != null) {
            assignedToName = userRepository.findById(task.getAssignedTo())
                    .map(User::getFullName).orElse("");
        }
        
        String assignedByName = "";
        if (task.getAssignedBy() != null) {
            assignedByName = userRepository.findById(task.getAssignedBy())
                    .map(User::getFullName).orElse("");
        }
        
        String createdByName = userRepository.findById(task.getCreatedBy())
                .map(User::getFullName).orElse("");
        
        String takenByName = "";
        if (task.getTakenBy() != null) {
            takenByName = userRepository.findById(task.getTakenBy())
                    .map(User::getFullName).orElse("");
        }
        
        TaskResponse response = new TaskResponse(task, assignedToName, assignedByName, createdByName);
        response.setTakenBy(task.getTakenBy());
        response.setTakenByName(takenByName);
        
        // Add attachment info
        response.setAttachmentUrl(task.getAttachmentUrl());
        response.setAttachmentName(task.getAttachmentName());
        
        return response;
    }
    
    
    
}