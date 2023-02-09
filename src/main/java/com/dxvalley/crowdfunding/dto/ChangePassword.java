package com.dxvalley.crowdfunding.dto;

import lombok.*;

@Data
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Getter
@Setter
public class ChangePassword {
    private String newPassword;
    private String oldPassword;
}
