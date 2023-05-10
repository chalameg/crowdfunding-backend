package com.dxvalley.crowdfunding.user.dto;

import com.dxvalley.crowdfunding.user.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class UserDTO {
    private Long userId;
    private String username;
    private String fullName;
    private String biography;
    private String website;
    private String createdAt;
    private String editedAt;
    private String verified;
    private UserStatus userStatus;
    private String avatarUrl;
    private String address;
    private Collection<String> roles;
    private Short totalCampaigns;
    private Short contributions;
    private Double totalAmountSpent;

}
