package com.dxvalley.crowdfunding.userManager.role;

import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.userManager.authority.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    // Retrieves all roles.
    public List<Role> getRoles() {
        List<Role> roles = roleRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        if (roles.isEmpty())
            throw new ResourceNotFoundException("No roles found.");

        return roles;
    }


    // Retrieves a role by its name.
    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found."));
    }

    // Retrieves a role by id.
    public Role getRoleById(Short roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found."));
    }


    // Adds a new role.
    public Role addRole(RoleDTO roleDTO) {
        Role role = new Role(roleDTO.getName());

        if (roleDTO.getAuthorities() == null || roleDTO.getAuthorities().isEmpty())
            throw new IllegalArgumentException("Authorities must be provided.");

        for (Authority authority : roleDTO.getAuthorities())
            role.addAuthority(authority);

        return roleRepository.save(role);
    }


    // Adds an authority to an existing role.
    public Role addAuthorityToRole(Short roleId, Authority authority) {
        Role role = getRoleById(roleId);

        role.addAuthority(authority);
        return roleRepository.save(role);
    }

    // Removes an authority from an existing role.
    public Role removeAuthorityFromRole(Short roleId, Authority authorityDto) {
        Role role = getRoleById(roleId);
        Collection<Authority> authorities = role.getAuthorities();
        authorities.removeIf(authority -> authority.getName().equals(authorityDto.getName()));
        role.setAuthorities(authorities);

        return roleRepository.save(role);
    }

}
