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
        public List<Users> getUsers();
        public Users getUserById(Long userId);
        public Users getUserByUsername(String username);
        public ResponseEntity<?> register(Users tempUser);
        public Boolean confirmToken(String token);
        public Users editUser(Long userId,  Users tempUser);
        public ResponseEntity<?> uploadUserAvatar(String userName, MultipartFile userAvatar);
        public ApiResponse changePassword(String username, ChangePassword temp);
        public ApiResponse forgotPassword(String username);
        public ApiResponse resetPassword(ResetPassword resetPassword);
        public void delete(String username);

}



