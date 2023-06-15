package com.dxvalley.crowdfunding.userManager.userDTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationReq {
    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "Full Name is required")
    private String fullName;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    @NotEmpty(message = "Password is required")
    private String password;
}

