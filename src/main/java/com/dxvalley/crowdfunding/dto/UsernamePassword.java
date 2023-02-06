package com.dxvalley.crowdfunding.dto;

import lombok.*;

@Data
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Getter
@Setter
public class UsernamePassword {
    private String newUsername;
    private String newPassword;
    private String oldPassword;
}
