package com.dxvalley.crowdfunding.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.dxvalley.crowdfunding.models.Otp;
import com.dxvalley.crowdfunding.repositories.OtpRepository;
import com.dxvalley.crowdfunding.services.OtpService;

import static java.util.concurrent.TimeUnit.*;

import lombok.AllArgsConstructor;
import lombok.Data;
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

    @PostMapping("/send")
    public ResponseEntity<?> getUserInfo(@RequestBody OtpRequest otpRequest) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:s");
        Date currentDate = new Date();
        Date expriryDate = new Date();
        if (this.otpRepository.findOtpByPhoneNumber(otpRequest.getPhoneNumber()) != null) {
            try {
                expriryDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:s")
                        .parse(this.otpRepository.findOtpByPhoneNumber(otpRequest.getPhoneNumber()).getExpiryDate());

            } catch (ParseException e) {
                e.printStackTrace();
            }

            long MAX_DURATION = MILLISECONDS.convert(2, MINUTES);
            long duration = currentDate.getTime() - expriryDate.getTime();

            if (duration <= MAX_DURATION) {
                otpService.deleteOtp(this.otpRepository.findOtpByPhoneNumber(otpRequest.getPhoneNumber()).getOtpId());
            }
        }
        if (this.otpRepository.findOtpByPhoneNumber(otpRequest.getPhoneNumber()) == null) {
            try {
                String uri = "http://10.1.245.150:7080/v1/otp/";

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String code = getRandomNumberString();
                
                String requestBody = "{\"mobile\":" + "\"" + otpRequest.getPhoneNumber() + "\"," + "\"text\":" + "\""
                        + code + "\"" + "}";
                System.out.println(requestBody);
                HttpEntity<String> request = new HttpEntity<String>(requestBody, headers);

                ResponseEntity<OtpResponse> res = restTemplate.exchange(uri, HttpMethod.POST, request,
                        OtpResponse.class);

                Calendar c = Calendar.getInstance();
                c.setTime(currentDate);
                c.add(Calendar.MINUTE, 0);
                Date expirayDate = c.getTime();
                System.out.println(dateFormat.format(currentDate));
                System.out.println(dateFormat.format(expirayDate));

                Otp otp = new Otp();
                otp.setPhoneNumber(otpRequest.getPhoneNumber());
                otp.setCode(code);
                otp.setExpiryDate(dateFormat.format(expirayDate));
                otpService.addOtp(otp);
                return new ResponseEntity<>(res.getBody(), HttpStatus.OK);
            } catch (Exception e) {
                OtpException response = new OtpException("error", e.getMessage());

                return new ResponseEntity<OtpException>(response, HttpStatus.NOT_FOUND);
            }
        } else {

            OtpException response = new OtpException("error", "otp already sent");
            return new ResponseEntity<OtpException>(response, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/confirm/{phoneNumber}")
    public ResponseEntity<ConfirmResponse> Confirm(@RequestBody ConfirmRequest request,
            @PathVariable String phoneNumber) {
        Otp otp = otpRepository.findOtpByPhoneNumber(phoneNumber);

        if (otp != null) {
            if (otp.getCode().equals(request.getCode())) {
                ConfirmResponse response = new ConfirmResponse("Verfied");
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

    public String getRandomNumberString() {

        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        return String.format("%06d", number);
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

@Data
class OtpResponse {
    private String Response;
    private String status;
    private String responseCode;
}

@Getter
@Setter
@AllArgsConstructor
class OtpException {
    private String state;
    private String message;
}