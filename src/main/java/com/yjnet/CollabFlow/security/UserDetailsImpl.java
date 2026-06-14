package com.yjnet.CollabFlow.security;

import com.yjnet.CollabFlow.entity.User;  // ← ADD THIS IMPORT
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String email;
    @JsonIgnore
    private String password;
    private String fullName;
    private String role;
    private Long tenantId;
    private Boolean isActive;
    private Collection<? extends GrantedAuthority> authorities;
    
    public UserDetailsImpl(Long id, String email, String password, String fullName, 
                          String role, Long tenantId, Boolean isActive) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.tenantId = tenantId;
        this.isActive = isActive;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }
    
    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getFullName(),
                user.getRole().name(),
                user.getTenantId(),
                user.getIsActive());
    }
    
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public Long getTenantId() { return tenantId; }
    public Boolean getIsActive() { return isActive; }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return email; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return isActive != null && isActive; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return isActive != null && isActive; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}