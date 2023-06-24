package com.dxvalley.crowdfunding.tokenManager;

import com.dxvalley.crowdfunding.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class ConfirmationTokenController {
    private final ConfirmationTokenService confirmationTokenService;

    @GetMapping({"/checkOtpExistence"})
    public ResponseEntity<ConfirmationToken> getOtpByCode(@RequestParam String otpCode) {
        return ResponseEntity.ok(this.confirmationTokenService.getToken(otpCode));
    }

    @PostMapping({"/sendConfirmationToken/{contact}"})
    public ResponseEntity<ApiResponse> sendConfirmationToken(@PathVariable String contact) {
        this.confirmationTokenService.sendConfirmationToken(contact);
        return ApiResponse.success("Confirmation token sent successfully");
    }

    @PutMapping({"/confirm"})
    public ResponseEntity<ApiResponse> confirmUser(@RequestParam String token) {
        return this.confirmationTokenService.confirmToken(token);
    }
}



