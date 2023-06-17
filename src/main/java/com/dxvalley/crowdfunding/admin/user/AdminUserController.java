package com.dxvalley.crowdfunding.admin.user;

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
    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(adminUserService.getUsers());
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<UserResponse> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(adminUserService.getUserById(userId));
    }

    @GetMapping("/username/{username}")
    ResponseEntity<UserResponse> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(adminUserService.getUserByUsername(username));
    }

    @PutMapping("/activate-ban/{userId}")
    public ResponseEntity<UserResponse> activate_ban(@PathVariable Long userId) {
        return ResponseEntity.ok(adminUserService.activate_ban(userId));
    }

    @DeleteMapping("/delete/{username}")
    ResponseEntity<ApiResponse> deleteUser(@PathVariable String username) {
        return adminUserService.delete(username);
    }

}

