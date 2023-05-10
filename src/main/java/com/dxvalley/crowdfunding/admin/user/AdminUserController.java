package com.dxvalley.crowdfunding.admin.user;

import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<?> getUsers() {
        return new ResponseEntity<>(adminUserService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<?> getByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(adminUserService.getUserById(userId), HttpStatus.OK);
    }

    @GetMapping("/username/{username}")
    ResponseEntity<?> getByUsername(@PathVariable String username) {
        return new ResponseEntity<>(adminUserService.getUserByUsername(username), HttpStatus.OK);
    }

    @PutMapping("/activate-ban/{userId}")
    public ResponseEntity<?> activate_ban(@PathVariable Long userId) {
        return new ResponseEntity<>(adminUserService.activate_ban(userId), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{username}")
    ResponseEntity<?> deleteUser(@PathVariable String username) {
        adminUserService.delete(username);
        return ApiResponse.success("User Deleted Successfully");
    }

}

