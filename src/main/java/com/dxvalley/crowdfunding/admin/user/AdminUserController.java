package com.dxvalley.crowdfunding.admin.user;

import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.userManager.user.UserRepository;
import com.dxvalley.crowdfunding.userManager.user.UserStatus;
import com.dxvalley.crowdfunding.userManager.user.UserUtils;
import com.dxvalley.crowdfunding.userManager.user.Users;
import com.dxvalley.crowdfunding.userManager.userDTO.UserMapper;
import com.dxvalley.crowdfunding.userManager.userDTO.UserResponse;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserRepository userRepository;
    private final UserUtils userUtils;

    public List<UserResponse> getUsers() {
        List<Users> users = this.userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("Currently, There is no User");
        } else {
            return users.stream().map(UserMapper::toUserResponse).toList();
        }
    }

    public UserResponse getUserById(Long userId) {
        return UserMapper.toUserResponse(this.userUtils.utilGetUserByUserId(userId));
    }

    public UserResponse getUserByUsername(String username) {
        return UserMapper.toUserResponse(this.userUtils.utilGetUserByUsername(username));
    }

    public UserResponse activate_ban(Long userId) {
        Users user = this.userUtils.utilGetUserByUserId(userId);
        if (user.getUserStatus().equals(UserStatus.ACTIVE)) {
            user.setUserStatus(UserStatus.BANNED);
        } else {
            user.setUserStatus(UserStatus.ACTIVE);
        }

        return UserMapper.toUserResponse((Users)this.userRepository.save(user));
    }

    public ResponseEntity<ApiResponse> delete(String username) {
        Users user = this.userUtils.utilGetUserByUsername(username);
        this.userRepository.delete(user);
        return ApiResponse.success("User Deleted Successfully");
    }
}

