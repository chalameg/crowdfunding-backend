package com.dxvalley.crowdfunding.user.role;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/Roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleRepository roleRepository;
    @GetMapping("/GetRole/{roleName}")
    Role getRoleByName (@PathVariable String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
    @GetMapping("/GetRoles")
    List<Role> getRoles () {
        return roleRepository.findAll();
    }
}
