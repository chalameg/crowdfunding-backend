package com.dxvalley.crowdfunding.services;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.dto.ResetPassword;
import com.dxvalley.crowdfunding.email.EmailSender;
import com.dxvalley.crowdfunding.exceptions.ResourceAlreadyExistsException;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.Otp;
import com.dxvalley.crowdfunding.models.Role;
import com.dxvalley.crowdfunding.models.Users;
import com.dxvalley.crowdfunding.models.ConfirmationToken;
import com.dxvalley.crowdfunding.repositories.ConfirmationTokenRepository;
import com.dxvalley.crowdfunding.repositories.OtpRepository;
import com.dxvalley.crowdfunding.repositories.RoleRepository;
import com.dxvalley.crowdfunding.repositories.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService {
        @Autowired
        private ConfirmationTokenService confirmationTokenService;
        @Autowired
        private EmailSender emailSender;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private RoleRepository roleRepo;
        @Autowired
        private PasswordEncoder passwordEncoder;
        @Autowired
        private ConfirmationTokenRepository confirmationTokenRepository;
        @Autowired
        OtpService otpService;
        @Autowired
        private OtpRepository otpRepository;

        public Users getUserById(Long userId){
               return userRepository.findByUserId(userId).orElseThrow(
                        () -> new ResourceNotFoundException("There is no user with this Id")
                );
        }
        public Users getUserByUsername(String username) {
                return userRepository.findByUsername(username).orElseThrow(
                        () -> new ResourceNotFoundException("There is no user with this username")
                );
        }

        public ResponseEntity<?> register(Users tempUser) {
                var user = userRepository.findUser(tempUser.getUsername());
                if (user.isPresent()){
                     throw new ResourceAlreadyExistsException("There is already a user with this username.");
                }

                // validate email and phone number before registration
                if (tempUser.getUsername().matches(".*[a-zA-Z]+.*")) {
                        // validate email
                } else {
                        if (tempUser.getUsername().length() < 9) {
                                createUserResponse response = new createUserResponse("error",
                                                "Please Inter a valid Mobile number!");
                                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                        }
                }


                List<Role> roles = new ArrayList<Role>(1);
                roles.add(this.roleRepo.findByRoleName("user"));
                tempUser.setRoles(roles);
                tempUser.setPassword(passwordEncoder.encode(tempUser.getPassword()));
                tempUser.setIsEnabled(false);
                userRepository.save(tempUser);

                // if user used email send email else send message to phone
                String token = UUID.randomUUID().toString();
                if (tempUser.getUsername().matches(".*[a-zA-Z]+.*")) {

                        ConfirmationToken confirmationToken = new ConfirmationToken(
                                        token,
                                        LocalDateTime.now(),
                                        LocalDateTime.now().plusMinutes(15),
                                        tempUser);

                        confirmationTokenService.saveConfirmationToken(
                                        confirmationToken);

                        String link = "http://localhost:8181/api/users/confirm?token=" + token;
                        emailSender.send(
                                        tempUser.getUsername(),
                                        emailSender.buildEmail(tempUser.getFullName(), link),
                                "Confirm your email");
                } else {
                        // send message here
                        ResponseEntity<String> res;
                        try {
                                String uri = "http://10.1.245.150:7080/v1/otp/";
                                RestTemplate restTemplate = new RestTemplate();

                                HttpHeaders headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);

                                String otp = getRandomNumberString();
                                String requestBody = "{\"mobile\":" + "\"" + tempUser.getUsername() + "\""
                                                + ",\"text\":" + "\"" + otp + "\"" + "}";

                                HttpEntity<String> request = new HttpEntity<String>(requestBody, headers);

                                res = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
                                System.out.println(res.getStatusCode().toString().trim().equals("200 OK"));

                                if (res.getStatusCode().toString().trim().equals("200 OK")) {
                                        // add otp to database
                                        tempUser.setOtp(otp);
                                        userRepository.save(tempUser);

                                        createUserResponse response = new createUserResponse("success",
                                                        "Message sent to your phone number.");
                                        return new ResponseEntity<>(response, HttpStatus.OK);
                                } else {
                                        createUserResponse response = new createUserResponse("error",
                                                        "Please inter valid Mobile number!");
                                        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                                }

                        } catch (Exception e) {
                                e.printStackTrace();
                                createUserResponse response = new createUserResponse("error",
                                                "Please insert valid Mobile number!!");
                                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                        }
                }

                return new ResponseEntity<>(
                                token,
                                HttpStatus.OK);
        }

        @Transactional
        public String confirmToken(String token) {
                ConfirmationToken confirmationToken = confirmationTokenService
                                .getToken(token);

                var result =  userRepository.findById(confirmationToken.getUser().getUserId()).get();
                if (result.getIsEnabled()) {
                        return "This email is already confirmed.";
                }
                LocalDateTime expiredAt = confirmationToken.getExpiresAt();
                if (expiredAt.isBefore(LocalDateTime.now())) {
                        return "This token has already expired";
                }
                confirmationTokenService.setConfirmedAt(token);
                var username = confirmationToken.getUser().getUsername();
                userRepository.enableUser(username);
                confirmationTokenRepository.delete(confirmationToken);
                return emailSender.emailConfirmed(confirmationToken.getUser().getFullName());
        }

        public String forgotPassword(String username) {
                Users user = userRepository.findUser(username).orElseThrow(
                        () -> new ResourceNotFoundException("There is no User with this username.")
                );
                if (username.matches(".*[a-zA-Z]+.*")) {
                        String token = UUID.randomUUID().toString();
                        ConfirmationToken confirmationToken = new ConfirmationToken(
                                token,
                                LocalDateTime.now(),
                                LocalDateTime.now().plusMinutes(15),
                                user);

                        confirmationTokenService.saveConfirmationToken(confirmationToken);
                        String link = "http://localhost:3000/resetPassword?token=" + token + "&username=" + user.getUsername();
                        emailSender.send(
                                user.getUsername(),
                                emailSender.emailBuilderForPasswordReset(user.getFullName(), link),
                                "Reset your password");
                        return "please check your email" + token;
                }else {
                        String code = getRandomNumberString();
                        Otp otp = new Otp(username, code);
                        otpService.addOtp(otp);
                        otpService.sendOtp(username,code);
                        System.out.println("last");
                        return "please check your phone";
                }
                }

        public ApiResponse resetPassword(ResetPassword resetPassword) {
                if (resetPassword.getUsername().matches(".*[a-zA-Z]+.*")) {
                        ConfirmationToken confirmationToken = confirmationTokenService
                                .getToken(resetPassword.getToken());

                        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
                        if (expiredAt.isBefore(LocalDateTime.now())) {
                                ApiResponse response = new ApiResponse(
                                        "success",
                                        "This token has already expired. Please reset again.");
                                return response;
                        }

                        var user = userRepository.findByUsername(confirmationToken.getUser().getUsername()).orElseThrow(
                                () -> new ResourceNotFoundException("There is no user with this username")
                        );
                        user.setPassword(passwordEncoder.encode(resetPassword.getPassword()));
                        userRepository.save(user);
                        confirmationTokenRepository.delete(confirmationToken);
                        ApiResponse response = new ApiResponse(
                                "success",
                                "Hooray! Your password has been successfully reset.");
                        return response;
                }

                else{
                        Otp otp = otpService.getOtpByCode(resetPassword.getToken());
                        Users user = userRepository.findByUsername(otp.getPhoneNumber()).orElseThrow(
                                () -> new ResourceNotFoundException("There is no User with this username")
                        );
                        user.setPassword(passwordEncoder.encode(resetPassword.getPassword()));
                        userRepository.save(user);
                        otpRepository.delete(otp);
                        ApiResponse response = new ApiResponse(
                                "success",
                                "Hooray! Your password has been successfully reset.");
                        return response;
                        }

        }

        public String getRandomNumberString() {
                Random rnd = new Random();
                int number = rnd.nextInt(999999);
                return String.format("%06d", number);
        }

}

@Getter
@Setter
@AllArgsConstructor
@Data
class ESBStatus {
        @JsonProperty("Response")
        public String response;
        public String status;
        public String responseCode;
}

@Getter
@Setter
@AllArgsConstructor
@Data
class SMSGatewayResponse {
        @JsonProperty("ESBStatus")
        public ESBStatus eSBStatus;
}

@Getter
@Setter
@AllArgsConstructor
@Data
class createUserResponse {
        String status;
        String description;
}
