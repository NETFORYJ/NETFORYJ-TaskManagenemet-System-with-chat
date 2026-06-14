package com.yjnet.CollabFlow.repository;

import com.yjnet.CollabFlow.entity.WorkspaceFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkspaceFileRepository extends JpaRepository<WorkspaceFile, Long> {
    List<WorkspaceFile> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
    void deleteByIdAndTenantId(Long id, Long tenantId);
}