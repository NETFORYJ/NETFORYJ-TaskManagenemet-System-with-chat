package com.yjnet.CollabFlow.repository;

import com.yjnet.CollabFlow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmailAndTenantId(String email, Long tenantId);
    
    Optional<User> findByGoogleId(String googleId);
    
    List<User> findByTenantId(Long tenantId);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmailAndTenantId(String email, Long tenantId);
    
    long countByTenantIdAndIsActiveTrue(Long tenantId);
}