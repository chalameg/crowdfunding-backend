package com.dxvalley.crowdfunding.userManager.user;

import com.dxvalley.crowdfunding.exception.customException.ForbiddenException;
import com.dxvalley.crowdfunding.exception.customException.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.messageManager.email.EmailService;
import com.dxvalley.crowdfunding.messageManager.sms.SmsService;
import com.dxvalley.crowdfunding.userManager.role.Role;
import com.dxvalley.crowdfunding.userManager.role.RoleService;
import com.dxvalley.crowdfunding.userManager.userDTO.UserRegistrationReq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserUtils {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final DateTimeFormatter dateTimeFormatter;
    private final EmailService emailService;
    private final SmsService smsService;

    public void validateUsernameAvailability(String username) {
        if (userRepository.findByUsername(username).isPresent())
            throw new ResourceAlreadyExistsException("There is already a user with this username.");
    }

    public void validateUsername(String username) {
        if (!emailService.isValidEmail(username) && !smsService.isValidPhoneNumber(username))
            throw new IllegalArgumentException("Username is neither a valid email nor a valid phone number.");
    }

    public Users createUser(UserRegistrationReq userRegistrationReq) {
        Role role = roleService.getRoleByName("USER");
        String encodedPassword = passwordEncoder.encode(userRegistrationReq.getPassword());

        return Users.builder()
                .username(userRegistrationReq.getUsername())
                .fullName(userRegistrationReq.getFullName())
                .password(encodedPassword)
                .userStatus(UserStatus.ACTIVE)
                .role(role)
                .createdAt(LocalDateTime.now().format(dateTimeFormatter))
                .build();
    }

    public Users utilGetUserByUserId(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with this Id"));

        return user;

    }

    public Users utilGetUserByUsername(String username) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("There is no user with this username."));

        return user;

    }


    public Users saveUser(Users user) {
        return userRepository.save(user);
    }

    public void delete(String username) {
        Users user = utilGetUserByUsername(username);
        userRepository.delete(user);
    }

    public void verifyUser(Users user) {
        if (!user.isVerified())
            throw new ForbiddenException("User is not verified");
    }
}
