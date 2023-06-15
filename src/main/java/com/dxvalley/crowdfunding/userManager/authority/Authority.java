package com.dxvalley.crowdfunding.userManager.authority;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "authorities", uniqueConstraints = {@UniqueConstraint(name = "Authority_name", columnNames = "name")})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Authority implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Short id;
    private String name;
    private String description;

    public Authority(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}

