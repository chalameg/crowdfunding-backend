package com.dxvalley.crowdfunding.userManager.userDTO;

import lombok.Data;

@Data
public class UserUpdateReq {
    private String fullName;

    private String biography;

    private String website;

    private String address;
}
