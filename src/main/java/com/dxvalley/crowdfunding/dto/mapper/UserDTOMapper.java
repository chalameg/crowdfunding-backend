package com.dxvalley.crowdfunding.dto.mapper;

import com.dxvalley.crowdfunding.dto.UserDTO;
import com.dxvalley.crowdfunding.model.Users;
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
        userDTO.setRoles(user.getRoles());
        userDTO.setAddress(user.getAddress());
        userDTO.setIsEnabled(user.getIsEnabled());
        userDTO.setEditedAt(user.getEditedAt());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setAvatarUrl(user.getAvatarUrl());
        userDTO.setBiography(user.getBiography());
        userDTO.setWebsite(user.getWebsite());

        return userDTO;
    }
}

