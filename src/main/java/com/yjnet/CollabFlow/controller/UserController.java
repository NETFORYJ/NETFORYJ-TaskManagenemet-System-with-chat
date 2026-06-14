package com.yjnet.CollabFlow.controller;

import com.yjnet.CollabFlow.entity.User;
import com.yjnet.CollabFlow.repository.UserRepository;
import com.yjnet.CollabFlow.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    private UserDetailsImpl getCurrentUser() {
        return (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
    
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        UserDetailsImpl currentUser = getCurrentUser();
        List<User> users = userRepository.findByTenantId(currentUser.getTenantId());
        
        List<UserDto> userDtos = users.stream()
            .filter(user -> user.getId() != currentUser.getId())
            .map(user -> new UserDto(user.getId(), user.getFullName(), user.getEmail()))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(userDtos);
    }
    
    // Inner DTO class
    static class UserDto {
        private Long id;
        private String name;
        private String email;
        
        public UserDto(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }
}