package com.yjnet.CollabFlow.repository;

import com.yjnet.CollabFlow.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    
    List<LoginHistory> findByUserIdOrderByLoginTimeDesc(Long userId);
    
    LoginHistory findTopByUserIdOrderByLoginTimeDesc(Long userId);
}