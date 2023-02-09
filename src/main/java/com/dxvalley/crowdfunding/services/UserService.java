package com.dxvalley.crowdfunding.services;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.ResetPassword;
import com.dxvalley.crowdfunding.dto.ChangePassword;
import com.dxvalley.crowdfunding.models.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
 public interface UserService {
         List<Users> getUsers();
         Users getUserById(Long userId);
         Users getUserByUsername(String username);
         ResponseEntity<?> register(Users tempUser);
         Boolean confirmToken(String token);
         Users editUser(Long userId,  Users tempUser);
         ResponseEntity<?> uploadUserAvatar(String userName, MultipartFile userAvatar);
         ApiResponse changePassword(String username, ChangePassword temp);
         ApiResponse forgotPassword(String username);
         ApiResponse resetPassword(ResetPassword resetPassword);
         void delete(String username);

}



