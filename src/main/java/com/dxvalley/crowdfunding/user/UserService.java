package com.dxvalley.crowdfunding.user;

import com.dxvalley.crowdfunding.user.dto.ChangePassword;
import com.dxvalley.crowdfunding.user.dto.ResetPassword;
import com.dxvalley.crowdfunding.user.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserService {

    UserDTO getUserById(Long userId);

    UserDTO getUserByUsername(String username);

    Users utilGetUserByUsername(String username);

    Users utilGetUserByUserId(Long userId);

    UserDTO register(Users tempUser);

    boolean confirmToken(String token);

    UserDTO editUser(Long userId, UserDTO userDTO);

    UserDTO uploadUserAvatar(String userName, MultipartFile userAvatar);

    ResponseEntity changePassword(String username, ChangePassword temp);

    ResponseEntity forgotPassword(String username);

    ResponseEntity resetPassword(ResetPassword resetPassword);

    void delete(String username);

    Users saveUser(Users user);

}



