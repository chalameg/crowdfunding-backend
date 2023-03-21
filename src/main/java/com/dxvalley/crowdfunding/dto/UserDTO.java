package com.dxvalley.crowdfunding.dto;

import com.dxvalley.crowdfunding.model.Role;
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
    private Boolean isEnabled;
    private String avatarUrl;
    private String address;
    private Collection<Role> roles;
}
