package com.dxvalley.crowdfunding.repository;

import com.dxvalley.crowdfunding.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository <Role, Integer> {
    Role findByRoleName (String roleName);
}
