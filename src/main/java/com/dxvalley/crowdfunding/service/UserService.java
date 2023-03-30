package com.dxvalley.crowdfunding.service;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.ChangePassword;
import com.dxvalley.crowdfunding.dto.ResetPassword;
import com.dxvalley.crowdfunding.dto.UserDTO;
import com.dxvalley.crowdfunding.model.Users;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface UserService {
    List<UserDTO> getUsers();

    UserDTO getUserById(Long userId);

    UserDTO getUserByUsername(String username);

    Users utilGetUserByUsername(String username);

    Users utilGetUserByUserId(Long userId);

    UserDTO register(Users tempUser);

    boolean confirmToken(String token);

    UserDTO editUser(Long userId, UserDTO userDTO);

    UserDTO uploadUserAvatar(String userName, MultipartFile userAvatar);

    ApiResponse changePassword(String username, ChangePassword temp);

    ApiResponse forgotPassword(String username);

    ApiResponse resetPassword(ResetPassword resetPassword);

    void delete(String username);

}



