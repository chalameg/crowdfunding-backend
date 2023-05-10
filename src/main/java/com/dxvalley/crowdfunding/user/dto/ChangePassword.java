package com.dxvalley.crowdfunding.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
public class ChangePassword {
    private String newPassword;
    private String oldPassword;
}
