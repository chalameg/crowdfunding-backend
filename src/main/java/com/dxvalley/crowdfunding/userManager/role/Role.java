package com.dxvalley.crowdfunding.userManager.role;

import com.dxvalley.crowdfunding.userManager.authority.Authority;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "roles", uniqueConstraints = {@UniqueConstraint(name = "Role_name", columnNames = "name")})
@NoArgsConstructor
@Data
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Short id;
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_authorities",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private Collection<Authority> authorities = new ArrayList<>();

    public Role(String name) {
        this.name = name;
    }

    public void addAuthority(Authority authority) {
        authorities.add(authority);
    }
}
