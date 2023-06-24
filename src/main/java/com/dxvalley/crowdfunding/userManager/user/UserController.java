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

    @GetMapping({"/getUser/{userId}"})
    public ResponseEntity<UserResponse> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(this.userService.getUserById(userId));
    }

    @GetMapping({"/getUserByUsername/{username}"})
    ResponseEntity<UserResponse> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(this.userService.getUserByUsername(username));
    }

    @PostMapping({"/register"})
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRegistrationReq userRegistrationReq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.register(userRegistrationReq));
    }

    @PutMapping({"/edit/{userId}"})
    public ResponseEntity<UserResponse> editUser(@PathVariable Long userId, @RequestBody @Valid UserUpdateReq updateReq) {
        return ResponseEntity.ok(this.userService.editUser(userId, updateReq));
    }

    @PutMapping({"/uploadUserAvatar/{userName}"})
    public ResponseEntity<UserResponse> uploadUserAvatar(@PathVariable String userName, @RequestParam MultipartFile userAvatar) {
        return ResponseEntity.ok(this.userService.uploadUserAvatar(userName, userAvatar));
    }

    @PutMapping({"/changePassword/{userName}"})
    public ResponseEntity<ApiResponse> changePassword(@PathVariable String userName, @RequestBody @Valid ChangePassword changePassword) {
        return this.userService.changePassword(userName, changePassword);
    }

    @PostMapping({"/forgotPassword/{username}"})
    ResponseEntity<ApiResponse> forgotPassword(@PathVariable String username) {
        return this.userService.forgotPassword(username);
    }

    @PutMapping({"/resetPassword"})
    ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPassword resetPassword) {
        return this.userService.resetPassword(resetPassword);
    }

    @DeleteMapping({"/delete/{username}"})
    ResponseEntity<ApiResponse> deleteUser(@PathVariable String username) {
        return this.userService.delete(username);
    }

}

