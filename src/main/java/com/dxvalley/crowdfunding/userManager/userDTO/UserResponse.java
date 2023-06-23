package com.dxvalley.crowdfunding.userManager.userDTO;

import com.dxvalley.crowdfunding.userManager.user.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String biography;
    private String website;
    private String createdAt;
    private String editedAt;
    private String verified;
    private UserStatus userStatus;
    private String avatarUrl;
    private String address;
    private String role;
    private short campaigns;
    private short contributions;
    private double amountSpentInBirr;

}
