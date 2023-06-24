package com.dxvalley.crowdfunding.userManager.user;

import com.dxvalley.crowdfunding.userManager.authority.Authority;
import com.dxvalley.crowdfunding.userManager.role.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "AdminUser_username_unique", columnNames = "username")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String fullName;
    private String avatarUrl;
    private String biography;
    private String website;
    @Column(
            unique = true
    )
    private String email;
    private String address;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    private short totalCampaigns;
    private short contributions;
    private double totalAmountSpent;
    private String createdAt;
    private String editedAt;
    private String editedBy;
    private boolean verified;
    private String lastLogin;
    @OneToOne(fetch = FetchType.EAGER)
    private Role role;


    @Override
    public Collection<Authority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return userStatus.equals(UserStatus.ACTIVE);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return verified;
    }
}