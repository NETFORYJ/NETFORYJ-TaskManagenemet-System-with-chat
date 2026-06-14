package com.yjnet.CollabFlow.service;

import com.yjnet.CollabFlow.entity.User;
import com.yjnet.CollabFlow.entity.Tenant;
import com.yjnet.CollabFlow.entity.LoginHistory;
import com.yjnet.CollabFlow.repository.UserRepository;
import com.yjnet.CollabFlow.repository.TenantRepository;
import com.yjnet.CollabFlow.repository.LoginHistoryRepository;
import com.yjnet.CollabFlow.security.JwtUtils;
import com.yjnet.CollabFlow.security.UserDetailsImpl;
import com.yjnet.CollabFlow.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    TenantRepository tenantRepository;
    
    @Autowired
    LoginHistoryRepository loginHistoryRepository;
    
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    JwtUtils jwtUtils;
    
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest, HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Get tenant name
        Tenant tenant = tenantRepository.findById(userDetails.getTenantId()).orElse(null);
        String tenantName = tenant != null ? tenant.getName() : "Unknown";
        
        // Save login history
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setUserId(userDetails.getId());
        loginHistory.setIpAddress(getClientIp(request));
        loginHistory.setUserAgent(request.getHeader("User-Agent"));
        loginHistory.setLoginType(LoginHistory.LoginType.JWT);
        loginHistoryRepository.save(loginHistory);
        
        // Update last login time
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user != null) {
            user.setLastLogin(java.time.LocalDateTime.now());
            userRepository.save(user);
        }
        
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getFullName(),
                userDetails.getRole(),
                userDetails.getTenantId(),
                tenantName));
    }
    
    @Transactional
    public ResponseEntity<?> registerUser(SignupRequest signupRequest, HttpServletRequest request) {
        // Check if tenant exists, if not create one
        Tenant tenant = tenantRepository.findBySubdomain(signupRequest.getSubdomain())
                .orElse(null);
        
        if (tenant == null) {
            tenant = new Tenant();
            tenant.setName(signupRequest.getTenantName());
            tenant.setSubdomain(signupRequest.getSubdomain());
            tenant = tenantRepository.save(tenant);
        }
        
        // Check if user exists
        if (userRepository.existsByEmailAndTenantId(signupRequest.getEmail(), tenant.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use!"));
        }
        
        // Create new user
        User user = new User();
        user.setTenantId(tenant.getId());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        user.setFullName(signupRequest.getFullName());
        user.setRole(User.Role.EMPLOYEE);
        
        // Set role if provided
        if (signupRequest.getRole() != null) {
            try {
                user.setRole(User.Role.valueOf(signupRequest.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setRole(User.Role.EMPLOYEE);
            }
        }
        
        user.setIsActive(true);
        userRepository.save(user);
        
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    
    @Transactional
    public void logoutUser(Long userId, HttpServletRequest request) {
        LoginHistory latestLogin = loginHistoryRepository.findTopByUserIdOrderByLoginTimeDesc(userId);
        if (latestLogin != null && latestLogin.getLogoutTime() == null) {
            latestLogin.setLogoutTime(java.time.LocalDateTime.now());
            loginHistoryRepository.save(latestLogin);
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}