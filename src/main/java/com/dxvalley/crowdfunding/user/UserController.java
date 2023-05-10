package com.dxvalley.crowdfunding.user;

import com.dxvalley.crowdfunding.user.dto.UserDTO;
import com.dxvalley.crowdfunding.utils.ApiResponse;
import com.dxvalley.crowdfunding.user.dto.ChangePassword;
import com.dxvalley.crowdfunding.user.dto.ResetPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {
  @Autowired
  private UserService userService;

  @GetMapping("/getUser/{userId}")
  public ResponseEntity<?> getByUserId(@PathVariable Long userId) {
    return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
  }

  @GetMapping("/getUserByUsername/{username}")
  ResponseEntity<?> getByUsername(@PathVariable String username) {
    return new ResponseEntity<>(userService.getUserByUsername(username), HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Users user) {
    return new ResponseEntity<>(userService.register(user), HttpStatus.CREATED);
  }

  @PutMapping("/confirm")
  public ResponseEntity<?> confirmUser(@RequestParam String token) {
    var isConfirmed = userService.confirmToken(token);
    if (isConfirmed) {
      return ApiResponse.success( "Confirmed Successfully");
    }
    return ApiResponse.error(HttpStatus.BAD_REQUEST, "This token has already expired");
  }

  @PutMapping("/edit/{userId}")
  public ResponseEntity<?> editUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
    return new ResponseEntity<>(userService.editUser(userId, userDTO), HttpStatus.OK);
  }

  @PutMapping("/uploadUserAvatar/{userName}")
  public ResponseEntity<?> uploadUserAvatar(@PathVariable String userName, @RequestParam MultipartFile userAvatar){
    return new ResponseEntity<>(userService.uploadUserAvatar(userName,userAvatar), HttpStatus.OK);
  }

  @PutMapping("/changePassword/{userName}")
  public ResponseEntity<?> manageAccount(@PathVariable String userName, @RequestBody ChangePassword temp) {
    return userService.changePassword(userName,temp);
  }

  @PostMapping("/forgotPassword/{username}")
  ResponseEntity<?> forgotPassword(@PathVariable String username) {
    return userService.forgotPassword(username);
  }

  @PutMapping("/resetPassword")
  ResponseEntity<?> resetPassword(@RequestBody ResetPassword resetPassword) {
    return userService.resetPassword(resetPassword);
  }

  @DeleteMapping("/delete/{username}")
  ResponseEntity<?> deleteUser(@PathVariable String username) {
    userService.delete(username);
    return ApiResponse.success("User Deleted Successfully");
  }

}

