package com.dxvalley.crowdfunding.controller;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.ChangePassword;
import com.dxvalley.crowdfunding.dto.ResetPassword;
import com.dxvalley.crowdfunding.dto.UserDTO;
import com.dxvalley.crowdfunding.model.Users;
import com.dxvalley.crowdfunding.service.UserService;
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

  @GetMapping("/getUsers")
  public ResponseEntity<?> getUsers() {
    return new ResponseEntity<>(userService.getUsers(),HttpStatus.OK);
  }

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
    if(isConfirmed){
      return new ResponseEntity<>(
              new ApiResponse("success", "Confirmed Successfully"),
              HttpStatus.OK);
    }
    return new ResponseEntity<>(
            new ApiResponse("bad request", "This token has already expired"),
            HttpStatus.BAD_REQUEST);
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
    var result = userService.changePassword(userName,temp);
    if(result.getStatus() == "error")
      return  new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping("/forgotPassword/{username}")
  ResponseEntity<?> forgotPassword(@PathVariable String username) {
    var result = userService.forgotPassword(username);
    if(result.getStatus() == "success")
      return new ResponseEntity<>(result, HttpStatus.OK);

    if(result.getStatus() == "error")
      return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);

    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);

  }

  @PutMapping("/resetPassword")
  ResponseEntity<?> resetPassword(@RequestBody ResetPassword resetPassword) {
    var result = userService.resetPassword(resetPassword);
    if (result.getStatus().equals("success"))
      return new ResponseEntity<>(result, HttpStatus.OK);
    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
  }

  @DeleteMapping("/delete/{username}")
  ResponseEntity<?> deleteUser(@PathVariable String username) {
    userService.delete(username);
    ApiResponse response = new ApiResponse("success","User Deleted Successfully");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

}

