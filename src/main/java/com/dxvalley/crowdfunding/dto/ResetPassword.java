package com.dxvalley.crowdfunding.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPassword {
    @NotEmpty(message = "password is required field")
    private String password;
    @NotEmpty(message = "Token is required field")
    private String token;
}
