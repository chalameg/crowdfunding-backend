package com.dxvalley.crowdfunding.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ResetPassword {
    @NotEmpty(message = "password is required field")
    private String password;
    @NotEmpty(message = "Token is required field")
    private String token;
}
