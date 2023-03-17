package com.dxvalley.crowdfunding.controller;

import com.dxvalley.crowdfunding.service.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/otp")
public class OtpController {
    @Autowired
    ConfirmationTokenService confirmationTokenService;

    @GetMapping("/checkOtpExistence")
    public ResponseEntity<?> getOtpByCode(@RequestParam String otpCode) {
        return new ResponseEntity<>(confirmationTokenService.getToken(otpCode), HttpStatus.OK);
    }
}



