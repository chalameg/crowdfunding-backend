package com.dxvalley.crowdfunding.controllers;

import com.dxvalley.crowdfunding.services.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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



