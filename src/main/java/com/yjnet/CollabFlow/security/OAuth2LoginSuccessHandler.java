package com.yjnet.CollabFlow.security;

import com.yjnet.CollabFlow.entity.Tenant;
import com.yjnet.CollabFlow.entity.User;
import com.yjnet.CollabFlow.repository.TenantRepository;
import com.yjnet.CollabFlow.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String googleId = oauth2User.getAttribute("sub");
        
        System.out.println("Google login success for: " + email);
        
        // Get or create default tenant
        Tenant tenant = tenantRepository.findById(1L).orElse(null);
        if (tenant == null) {
            tenant = new Tenant();
            tenant.setId(1L);
            tenant.setName("Default Workspace");
            tenant.setSubdomain("default");
            tenantRepository.save(tenant);
        }
        
        // Find or create user
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            // Create new user
            user = new User();
            user.setEmail(email);
            user.setFullName(name);
            user.setGoogleId(googleId);
            user.setRole(User.Role.EMPLOYEE);
            user.setIsActive(true);
            user.setTenantId(1L);
            userRepository.save(user);
            System.out.println("Created new user: " + email);
        } else if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
            userRepository.save(user);
            System.out.println("Linked Google account to: " + email);
        }
        
        // Generate JWT token
        String jwtToken = jwtUtils.generateTokenFromEmail(email);
        
        // Redirect to frontend with token
        response.sendRedirect("/oauth2-success.html?token=" + jwtToken);
    }
}