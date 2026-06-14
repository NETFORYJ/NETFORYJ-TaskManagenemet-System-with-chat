package com.yjnet.CollabFlow.repository;

import com.yjnet.CollabFlow.entity.Task; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByTenantId(Long tenantId);
    
    List<Task> findByTenantIdAndAssignedTo(Long tenantId, Long assignedTo);
    
    List<Task> findByTenantIdAndStatus(Long tenantId, Task.TaskStatus status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.tenantId = :tenantId AND t.status = 'AVAILABLE'")
    long countAvailableTasks(@Param("tenantId") Long tenantId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.tenantId = :tenantId AND t.status = 'IN_PROGRESS'")
    long countInProgressTasks(@Param("tenantId") Long tenantId);
}