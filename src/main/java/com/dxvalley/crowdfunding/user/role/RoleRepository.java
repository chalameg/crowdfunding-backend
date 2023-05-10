package com.dxvalley.crowdfunding.user.role;

import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository <Role, Integer> {
    Role findByRoleName (String roleName);
}
