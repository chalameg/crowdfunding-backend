package com.dxvalley.crowdfunding.userManager.role;

import com.dxvalley.crowdfunding.exception.DatabaseAccessException;
import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.userManager.authority.Authority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static com.dxvalley.crowdfunding.utils.ApplicationConstants.DB_ERROR_MESSAGE;
import static com.dxvalley.crowdfunding.utils.LoggingUtils.logError;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;

    /**
     * Retrieves all roles.
     *
     * @return A list of roles.
     * @throws DatabaseAccessException   if there is an error accessing the database.
     * @throws ResourceNotFoundException if no roles are found.
     */
    public List<Role> getRoles() {
        try {
            List<Role> roles = roleRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

            if (roles.isEmpty())
                throw new ResourceNotFoundException("No roles found.");

            return roles;
        } catch (DataAccessException ex) {
            logError(getClass(), "getRoles", ex);
            throw new DatabaseAccessException(DB_ERROR_MESSAGE);
        }
    }


    /**
     * Retrieves a role by its name.
     *
     * @param roleName The name of the role to retrieve.
     * @return The found role.
     * @throws DatabaseAccessException   if there is an error accessing the database.
     * @throws ResourceNotFoundException if the role is not found.
     */
    public Role getRoleByName(String roleName) {
        try {
            return roleRepository.findByName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found."));
        } catch (DataAccessException ex) {
            logError(getClass(), "getRoleByName", ex);
            throw new DatabaseAccessException(DB_ERROR_MESSAGE);
        }
    }


    /**
     * Retrieves a role by id.
     *
     * @param roleId The id of the role to retrieve.
     * @return The found role.
     * @throws DatabaseAccessException   if there is an error accessing the database.
     * @throws ResourceNotFoundException if the role is not found.
     */
    public Role getRoleById(Short roleId) {
        try {
            return roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found."));
        } catch (DataAccessException ex) {
            logError(getClass(), "getRoleById", ex);
            throw new DatabaseAccessException(DB_ERROR_MESSAGE);
        }
    }


    /**
     * Adds a new role.
     *
     * @param roleDTO The RoleDTO containing the role information.
     * @return The added role.
     * @throws DatabaseAccessException if there is an error accessing the database.
     */
    public Role addRole(RoleDTO roleDTO) {
        try {
            Role role = new Role(roleDTO.getName());

            if (roleDTO.getAuthorities() == null || roleDTO.getAuthorities().isEmpty())
                throw new IllegalArgumentException("Authorities must be provided.");

            for (Authority authority : roleDTO.getAuthorities()) {
                role.addAuthority(authority);
            }

            return roleRepository.save(role);
        } catch (DataAccessException ex) {
            logError(getClass(), "addRole", ex);
            throw new DatabaseAccessException(DB_ERROR_MESSAGE);
        }
    }


    /**
     * Adds an authority to an existing role.
     *
     * @param roleId    The ID of the role to add the authority to.
     * @param authority The authority to be added.
     * @return The role with the added authority.
     * @throws DatabaseAccessException   if there is an error accessing the database.
     * @throws ResourceNotFoundException if the role is not found.
     */
    public Role addAuthorityToRole(Short roleId, Authority authority) {
        try {
            Role role = getRoleById(roleId);

            role.addAuthority(authority);
            return roleRepository.save(role);
        } catch (DataAccessException ex) {
            logError(getClass(), "addAuthorityToRole", ex);
            throw new DatabaseAccessException(DB_ERROR_MESSAGE);
        }
    }

    /**
     * Removes an authority from an existing role.
     *
     * @param roleId       The ID of the role to remove the authority from.
     * @param authorityDto The authority to be removed.
     * @return The role with the authority removed.
     * @throws DatabaseAccessException   if there is an error accessing the database.
     * @throws ResourceNotFoundException if the role is not found.
     */
    public Role removeAuthorityFromRole(Short roleId, Authority authorityDto) {
        try {
            Role role = getRoleById(roleId);
            Collection<Authority> authorities = role.getAuthorities();
            authorities.removeIf(authority -> authority.getName().equals(authorityDto.getName()));
            role.setAuthorities(authorities);

            return roleRepository.save(role);
        } catch (DataAccessException ex) {
            logError(getClass(), "removeAuthorityFromRole", ex);
            throw new DatabaseAccessException(DB_ERROR_MESSAGE);
        }
    }

}
