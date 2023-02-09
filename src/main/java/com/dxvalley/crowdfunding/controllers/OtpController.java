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

    @GetMapping("/checkOtpExistance")
    public ResponseEntity<?>getOtpByCode(@RequestParam String token) {
        return new ResponseEntity<>(
                confirmationTokenService.getToken(token),
                HttpStatus.OK);
    }
}




