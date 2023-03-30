package com.dxvalley.crowdfunding.controller;

import com.dxvalley.crowdfunding.dto.ApiResponse;
import com.dxvalley.crowdfunding.service.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
public class ConfirmationTokenController {
    @Autowired
    ConfirmationTokenService confirmationTokenService;

    @GetMapping("/checkOtpExistence")
    public ResponseEntity<?> getOtpByCode(@RequestParam String otpCode) {
        return new ResponseEntity<>(confirmationTokenService.getToken(otpCode), HttpStatus.OK);
    }

    @PostMapping("/sendConfirmationToken/{username}")
    public ResponseEntity<?> sendConfirmationToken(@PathVariable String username) {
        confirmationTokenService.sendConfirmationToken(username);
        ApiResponse response = new ApiResponse("success", "Confirmation token sent successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}



