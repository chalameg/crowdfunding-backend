package com.dxvalley.crowdfunding.controllers;

import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dxvalley.crowdfunding.models.Otp;
import com.dxvalley.crowdfunding.repositories.OtpRepository;
import com.dxvalley.crowdfunding.services.OtpService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("/api/otp")
public class OtpController {
    private final OtpService otpService;
    private final OtpRepository otpRepository;

    public OtpController(OtpService otpService, OtpRepository otpRepository) {
        this.otpService = otpService;
        this.otpRepository = otpRepository;
    }


    @GetMapping("/getOtpByCode")
    public ResponseEntity<?>getOtpByCode(@RequestParam String optCode) {
        return new ResponseEntity<>(
                otpService.getOtpByCode(optCode),
                HttpStatus.OK);
    }

    @PostMapping("/send")
    public ResponseEntity<?> getUserInfo(@RequestBody OtpRequest otpRequest) {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        var randomNumber = String.format("%06d", number);
        return otpService.sendOtp(otpRequest.getPhoneNumber(),randomNumber);
    }

    @PostMapping("/confirm/{phoneNumber}")
    public ResponseEntity<ConfirmResponse> Confirm(@RequestBody ConfirmRequest request,
            @PathVariable String phoneNumber) {

        System.out.println(phoneNumber + " " + request.getCode());
        Otp otp = otpRepository.findOtpByPhoneNumberAndByCode(phoneNumber, request.getCode());

        System.out.println(otp + " bitch");
        if (otp != null) {
            if (otp.getCode().equals(request.getCode())) {
                ConfirmResponse response = new ConfirmResponse("Verified");
                otpRepository.delete(otp);
                return new ResponseEntity<ConfirmResponse>(response, HttpStatus.OK);
            } else {
                ConfirmResponse response = new ConfirmResponse("Incorrect code");
                return new ResponseEntity<ConfirmResponse>(response, HttpStatus.BAD_REQUEST);
            }
        } else {
            ConfirmResponse response = new ConfirmResponse("Request for code first");
            return new ResponseEntity<ConfirmResponse>(response, HttpStatus.BAD_REQUEST);
        }
    }

}

@Getter
@Setter
@AllArgsConstructor
class OtpRequest {
    private String phoneNumber;
}

@Getter
@Setter
@AllArgsConstructor
class ConfirmRequest {
    private String code;
}

@Getter
@Setter
@AllArgsConstructor
class ConfirmResponse {
    private String status;
}

