package com.dxvalley.crowdfunding.userManager.user;

import com.dxvalley.crowdfunding.userManager.userDTO.*;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping({"/me"})
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(userService.me());
    }

    @PostMapping({"/register"})
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRegistrationReq userRegistrationReq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(userRegistrationReq));
    }

    @PutMapping({"/edit"})
    public ResponseEntity<UserResponse> editUser(@RequestBody @Valid UserUpdateReq updateReq) {
        return ResponseEntity.ok(userService.editUser(updateReq));
    }

    @PutMapping({"/uploadAvatar"})
    public ResponseEntity<UserResponse> uploadAvatar(@RequestParam MultipartFile userAvatar) {
        return ResponseEntity.ok(userService.uploadAvatar(userAvatar));
    }

    @PutMapping({"/changePassword"})
    public ResponseEntity<ApiResponse> changePassword(@RequestBody @Valid ChangePassword changePassword) {
        return userService.changePassword(changePassword);
    }

    @PostMapping({"/forgotPassword/{username}"})
    ResponseEntity<ApiResponse> forgotPassword(String username) {
        return userService.forgotPassword(username);
    }

    @PutMapping({"/resetPassword"})
    ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPassword resetPassword) {
        return userService.resetPassword(resetPassword);
    }
}

