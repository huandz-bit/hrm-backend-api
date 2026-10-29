package com.hrm.security;

import com.hrm.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private String fullName;
    private Long departmentId;
    private String systemRole;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(User user) {
        String role = user.getSystemRole() != null ? user.getSystemRole() : "EMPLOYEE";
        String roleAuthority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .fullName(user.getFullName())
                .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
                .systemRole(user.getSystemRole())
                .authorities(List.of(new SimpleGrantedAuthority(roleAuthority)))
                .build();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
