package com.dxvalley.crowdfunding.userManager.user;

import com.dxvalley.crowdfunding.userManager.userDTO.*;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserService {
    UserResponse getUserById(Long userId);

    UserResponse getUserByUsername(String username);

    UserResponse register(UserRegistrationReq userRegistrationReq);

    UserResponse editUser(Long userId, UserUpdateReq updateReq);

    UserResponse uploadUserAvatar(String userName, MultipartFile userAvatar);

    ResponseEntity<ApiResponse> changePassword(String username, ChangePassword temp);

    ResponseEntity<ApiResponse> forgotPassword(String username);

    ResponseEntity<ApiResponse> resetPassword(ResetPassword resetPassword);

    ResponseEntity<ApiResponse> delete(String username);

}



