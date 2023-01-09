package com.dxvalley.crowdfunding.repositories;

import org.springframework.data.jpa.repository.JpaRepository;


import com.dxvalley.crowdfunding.models.Role;


public interface RoleRepository extends JpaRepository <Role, Integer> {
    Role findByRoleName (String roleName);
}
