package com.dxvalley.crowdfunding.user.dto;

import com.dxvalley.crowdfunding.user.Users;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserDTOMapper implements Function<Users, UserDTO> {
    @Override
    public UserDTO apply(Users user) {
        var userDTO = new UserDTO();

        userDTO.setUserId(user.getUserId());
        userDTO.setUsername(user.getUsername());
        userDTO.setFullName(user.getFullName());
        userDTO.setAddress(user.getAddress());
        userDTO.setVerified(user.getIsEnabled() ? "YES" : "NO");
        userDTO.setUserStatus(user.getUserStatus());
        userDTO.setEditedAt(user.getEditedAt());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setAvatarUrl(user.getAvatarUrl());
        userDTO.setBiography(user.getBiography());
        userDTO.setTotalCampaigns(user.getTotalCampaigns());
        userDTO.setContributions(user.getContributions());
        userDTO.setTotalAmountSpent(user.getTotalAmountSpent());

        return userDTO;
    }
}

