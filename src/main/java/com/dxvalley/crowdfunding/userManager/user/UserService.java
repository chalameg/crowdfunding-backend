package com.dxvalley.crowdfunding.userManager.user;

import com.dxvalley.crowdfunding.userManager.userDTO.*;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse me();
    UserResponse register(UserRegistrationReq userRegistrationReq);

    UserResponse editUser(UserUpdateReq updateReq);

    UserResponse uploadAvatar(MultipartFile userAvatar);

    ResponseEntity<ApiResponse> changePassword(ChangePassword temp);

    ResponseEntity<ApiResponse> forgotPassword();

    ResponseEntity<ApiResponse> resetPassword(ResetPassword resetPassword);
}



