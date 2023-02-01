package com.dxvalley.crowdfunding.controllers;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.dxvalley.crowdfunding.services.FileUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.dxvalley.crowdfunding.models.Users;
import com.dxvalley.crowdfunding.repositories.UserRepository;
import com.dxvalley.crowdfunding.services.UserRegistrationService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserRegistrationService registrationService;
  private final FileUploadService fileUploadService;

  private boolean isSysAdmin() {
    AtomicBoolean hasSysAdmin = new AtomicBoolean(false);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    auth.getAuthorities().forEach(grantedAuthority -> {
      if (grantedAuthority.getAuthority().equals("sysAdmin")) {
        hasSysAdmin.set(true);
      }
    });
    return hasSysAdmin.get();
  }

  @GetMapping("/getUsers")
  List<Users> getUsers() {
    if (isSysAdmin()) {
      return this.userRepository.findAll();
    }
    var users = this.userRepository.findAll();

    users.removeIf(user -> {
      var containsAdmin = false;
      for (var role : user.getRoles()) {
        containsAdmin = containsAdmin || role.getRoleName().equals("admin");
      }
      return containsAdmin;
    });
    return users;
  }

  @GetMapping("/getUser/{userId}")
  public ResponseEntity<?> getByUserId(@PathVariable Long userId) {
    var user = userRepository.findByUserId(userId);
    if (user == null) {
      ApiResponse response = new ApiResponse("error", "Cannot find user with this user ID!");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @GetMapping("/getUserByUsername/{username}")
  ResponseEntity<?> getByUsername(@PathVariable String username) {
    var user = userRepository.findByUsername(username);

    if (user == null) {
      ApiResponse response = new ApiResponse("error", "Cannot find user with this phone number/email!");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody Users tempUser) {

    return registrationService.register(tempUser);
  }

  @GetMapping("/confirm")
  public String confirmUser(@RequestParam("token") String token) {

    return registrationService.confirmToken(token);
  }

  @PostMapping("/confirmPhone/{phoneNumber}")
  public ResponseEntity<?> confirmOTP(@RequestParam("otp") String otp, @PathVariable String phoneNumber){

    Users user = userRepository.findUserByUsername(phoneNumber);

    if(user.getOtp().equals(otp)){
      //make enable true here
      user.setIsEnabled(true);

      userRepository.save(user);

      ApiResponse response = new ApiResponse("success", "Otp Confirmed.");
      return new ResponseEntity<>(response, HttpStatus.OK);
    }
    ApiResponse response = new ApiResponse("error", "Cannot confirm the otp provided.");
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @PutMapping("/edit/{userId}")
  public ResponseEntity<?> editUser(
          @PathVariable Long userId,
          @RequestParam(required = false) MultipartFile userAvatar,
          @RequestParam(required = false) String username,
          @RequestParam(required = false) String fullName,
          @RequestParam(required = false) String biography,
          @RequestParam(required = false) String website,
          @RequestParam(required = false) String address
  ) {

    Users user = userRepository.findByUserId(userId);
    if (user == null) {
      return new ResponseEntity<>("Cannot find user with this ID!", HttpStatus.BAD_REQUEST);
    }

    String avatarUrl;
    if(userAvatar != null){
      try {
        avatarUrl = fileUploadService.uploadFile(userAvatar);
      } catch (Exception e) {
        ApiResponse response = new ApiResponse("error", "Bad file size or format!");
        return new ResponseEntity<>(response, HttpStatus.OK);
      }
    }else{
      avatarUrl = null;
    }

    user.setUsername(username != null ? username : user.getUsername());
    user.setFullName(fullName != null ? fullName : user.getFullName());
    user.setBiography(biography != null ? biography : user.getBiography());
    user.setWebsite(website != null ? website : user.getWebsite());
    user.setAvatarUrl(avatarUrl != null? avatarUrl : user.getAvatarUrl());
    user.setAddress(address != null ? address : user.getAddress());
    Users response = userRepository.save(user);
    response.setPassword(null);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping("/changePassword/{userName}")
  public ResponseEntity<?> manageAccount(
          @RequestBody UsernamePassword temp,
          @PathVariable String userName) throws AccessDeniedException {
    Users user = userRepository.findByUsername(userName);
    if (user == null){
      ApiResponse response = new ApiResponse("error", "Cannot find user with this username!");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    if (passwordEncoder.matches(temp.getOldPassword(), user.getPassword())) {
      user.setPassword(passwordEncoder.encode(temp.getNewPassword()));
    }else{
      ApiResponse response = new ApiResponse("error", "Incorrect old Password!");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    userRepository.save(user);
    ApiResponse response = new ApiResponse("success", "Password Changed Successfully!");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @DeleteMapping("/delete/{username}")
  ResponseEntity<?> deleteUser(@PathVariable String username) {
    Users user = userRepository.findUser(username);
    if (user == null) {
      return new ResponseEntity<>("Cannot find user with this username!", HttpStatus.BAD_REQUEST);
    }
    this.userRepository.deleteById(user.getUserId());
    return new ResponseEntity<>("Deleted", HttpStatus.OK);
  }

}

@Getter
@Setter
class UsernamePassword {
  private String newUsername;
  private String newPassword;
  private String oldPassword;
}

@Getter
@Setter
@AllArgsConstructor
class ApiResponse {
  String status;
  String description;
}