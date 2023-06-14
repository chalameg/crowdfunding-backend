package com.dxvalley.crowdfunding.userManager.role;

import com.dxvalley.crowdfunding.userManager.authority.Authority;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Collection;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDTO {
    private Short id;

    @NotBlank(message = "Role name is mandatory")
    private String name;

    @NotNull(message = "Authorities are mandatory")
    @Size(min = 1, message = "At least one authority must be provided")
    private Collection<Authority> authorities;

    public RoleDTO(String name) {
        this.name = name;
    }
}
