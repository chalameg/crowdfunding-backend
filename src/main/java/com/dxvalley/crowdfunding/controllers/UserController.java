package com.dxvalley.crowdfunding.controllers;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.dxvalley.crowdfunding.models.Users;
import com.dxvalley.crowdfunding.repositories.RoleRepository;
import com.dxvalley.crowdfunding.repositories.UserRepository;
import com.dxvalley.crowdfunding.services.UserRegistrationService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
  private final UserRepository userRepository;
  private final RoleRepository roleRepo;
  private final PasswordEncoder passwordEncoder;
  private final UserRegistrationService registrationService;

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

  @GetMapping("/getOtp/{phoneNumber}")
  public ResponseEntity<?> getOtp(@PathVariable String phoneNumber) {
    ResponseEntity<String> res;
    try {
      Users user = userRepository.findByUsername(phoneNumber);
      if (user == null){
        return new ResponseEntity<>("user does not exist!", HttpStatus.BAD_REQUEST);
      }

      String uri = "http://10.1.245.150:7080/v1/otp/";
      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      String otp = getRandomNumberString();

      String requestBody = "{\"mobile\":" + "\"" + user.getUsername() + "\""
          + ",\"text\":" + "\"" + otp + "\"" + "}";

      HttpEntity<String> request = new HttpEntity<String>(requestBody, headers);

      res = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

      if (res.getStatusCode().toString().trim().equals("200 OK")) {
        // add otp to database
        user.setOtp(otp);
        userRepository.save(user);

        ApiResponse response = new ApiResponse("success", "Message sent to your phone number.");

        return new ResponseEntity<>(response, HttpStatus.OK);

      } else {
        ApiResponse response = new ApiResponse("error","Please inter valid Mobile number!");

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
      }

    } catch (Exception e) {
      ApiResponse response = new ApiResponse("error","Please insert valid Mobile number!!");

      return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/confirmOtp/{phoneNumber}")
  public ResponseEntity<?> confirmOTP(@RequestParam("otp") String otp, @PathVariable String phoneNumber) {

    Users user = userRepository.findUserByUsername(phoneNumber);

    if (user.getOtp().equals(otp)) {
      // make enable true here
      user.setIsEnabled(true);

      userRepository.save(user);

      ApiResponse response = new ApiResponse("success", "Otp Confirmed.");
      return new ResponseEntity<>(response, HttpStatus.OK);
    }
    ApiResponse response = new ApiResponse("error", "Cannot confirm the otp provided.");
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @PutMapping("/edit/{userId}")
  public ResponseEntity<?> editUser(@RequestBody Users tempUser, @PathVariable Long userId) {
    Users user = userRepository.findByUserId(userId);
    if (user == null) {
      return new ResponseEntity<>("Cannot find user with this ID!", HttpStatus.BAD_REQUEST);
    }
    user.setUsername(tempUser.getUsername() != null ? tempUser.getUsername() : user.getUsername());

    user.setRoles(tempUser.getRoles() != null
        ? tempUser.getRoles().stream().map(x -> this.roleRepo.findByRoleName(x.getRoleName()))
            .collect(Collectors.toList())
        : user.getRoles().stream().map(x -> this.roleRepo.findByRoleName(x.getRoleName()))
            .collect(Collectors.toList()));

    user.setFullName(tempUser.getFullName() != null ? tempUser.getFullName() : user.getFullName());

    user.setBiography(tempUser.getBiography() != null ? tempUser.getBiography() : user.getBiography());

    user.setWebsite(tempUser.getWebsite() != null ? tempUser.getWebsite() : user.getWebsite());

    user.setPassword(tempUser.getPassword() != null ? passwordEncoder.encode(tempUser.getPassword())
        : passwordEncoder.encode(user.getPassword()));

    Users response = userRepository.save(user);

    response.setPassword(null);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping("/changePasswordOrUsername/{userName}")
  public ResponseEntity<?> manageAccount(@RequestBody UsernamePassword temp,
      @PathVariable String userName) throws AccessDeniedException {

    Users user = userRepository.findByUsername(userName);

    if (user == null) {
      ApiResponse response = new ApiResponse("error", "Cannot find user with this username!");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    if (passwordEncoder.matches(temp.getOldPassword(), user.getPassword())) {
      user.setPassword(passwordEncoder.encode(temp.getNewPassword()));
      System.out.println("password reset");
    } else {
      ApiResponse response = new ApiResponse("error", "Incorrect old Password!");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    user.setUsername(temp.getNewUsername() != null ? temp.getNewUsername() : user.getUsername());

    userRepository.save(user);

    ApiResponse response = new ApiResponse("success", "Password / userName Changed Successfully!");

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

  public String getRandomNumberString() {
  
    Random rnd = new Random();
    int number = rnd.nextInt(999999);
  
    return String.format("%06d", number);
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

