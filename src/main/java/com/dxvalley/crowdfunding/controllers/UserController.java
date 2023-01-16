package com.dxvalley.crowdfunding.controllers;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dxvalley.crowdfunding.models.Role;
import com.dxvalley.crowdfunding.models.Users;
import com.dxvalley.crowdfunding.repositories.RoleRepository;
import com.dxvalley.crowdfunding.repositories.UserRepository;

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

  private boolean isOwnAccount(String userName) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return userRepository.findByUsername((String) auth.getPrincipal()).getUsername().equals(userName);
  }

  @GetMapping("/getUsers")
  List<Users> getUsers() {
    if (isSysAdmin()) {
      return this.userRepository.findAll(Sort.by("username"));
    }
    var users = this.userRepository.findAll(Sort.by("username"));
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
      createUserResponse response = new createUserResponse("error", "Cannot find this user!");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @GetMapping("/getUserByUsername/{username}")
  Optional<Users> getByUsername(@PathVariable String username) {
    return Optional.ofNullable(userRepository.findByUsername(username));
  }

  @PostMapping("/register")
  public ResponseEntity<createUserResponse> register(@RequestBody Users tempUser) {
    var user = userRepository.findByUsername(tempUser.getUsername());
    if (user != null) {
      createUserResponse response = new createUserResponse("error", "user already exists");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    List<Role> roles = new ArrayList<Role>(1);
    roles.add(this.roleRepo.findByRoleName("user"));
    tempUser.setRoles(roles);
    tempUser.setPassword(passwordEncoder.encode(tempUser.getPassword()));
    userRepository.save(tempUser);
    //send email verification
    
    createUserResponse response = new createUserResponse("success", "user created successfully!");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping("/edit/{userId}")
  public ResponseEntity<?> editUser(@RequestBody Users tempUser, @PathVariable Long userId) {
    Users user = userRepository.findByUserId(userId);
    if(user == null){
      return new ResponseEntity<>("Cannot find user with this ID!", HttpStatus.BAD_REQUEST);
    }
    
    user.setUsername(tempUser.getUsername() != null ? tempUser.getUsername() : user.getUsername());

    user.setRoles(tempUser.getRoles() != null ? 
      tempUser.getRoles().stream().map(x -> this.roleRepo.findByRoleName(x.getRoleName())).collect(Collectors.toList()) : 
      user.getRoles().stream().map(x -> this.roleRepo.findByRoleName(x.getRoleName())).collect(Collectors.toList()));

    user.setFullName(tempUser.getFullName() != null ? tempUser.getFullName() : user.getFullName());

    user.setPassword(tempUser.getPassword() != null ? passwordEncoder.encode(tempUser.getPassword()) : passwordEncoder.encode(user.getPassword()));
   
    Users response = userRepository.save(user);

    response.setPassword(null);
    
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping("/manageAccount/{userName}/{usernameChange}")
  public Users manageAccount(@RequestBody UsernamePassword temp,
      @PathVariable String userName,
      @PathVariable Boolean usernameChange) throws AccessDeniedException {
    if (isOwnAccount(userName)) {
      Users user = userRepository.findByUsername(userName);
      if (passwordEncoder.matches(temp.getOldPassword(), user.getPassword())) {
        user.setPassword(passwordEncoder.encode(temp.getNewPassword()));
      }
      if (usernameChange) {
        user.setUsername(temp.getNewUsername());
      }
      Users response = userRepository.save(user);
      response.setPassword(null);
      return response;
    } else
      throw new AccessDeniedException("403 Forbidden");
  }

  @DeleteMapping("/delete/{userId}")
  ResponseEntity<?> deleteUser(@PathVariable Long userId) {
    Users user = userRepository.findByUserId(userId);
    if(user == null){
      return new ResponseEntity<>("Cannot find user with this ID!", HttpStatus.BAD_REQUEST);
    }
    this.userRepository.deleteById(userId);

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
class createUserResponse {
  String status;
  String description;
}
