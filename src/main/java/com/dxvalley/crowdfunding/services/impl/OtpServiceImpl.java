package com.dxvalley.crowdfunding.services.impl;

import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import com.dxvalley.crowdfunding.models.Otp;
import com.dxvalley.crowdfunding.repositories.OtpRepository;
import com.dxvalley.crowdfunding.services.OtpService;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class OtpServiceImpl implements OtpService{
    private final OtpRepository otpRepository;

    public OtpServiceImpl(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }
    
    @Override
    public Otp addOtp(Otp otp) {
        return this.otpRepository.save(otp);
    }

    @Override
    public ResponseEntity<?> sendOtp(String phoneNumber, String randomNumber) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:s");
        Date currentDate = new Date();
        Date expriryDate = new Date();
        if (this.otpRepository.findOtpByPhoneNumberAndByCode(phoneNumber,randomNumber) != null) {
//            try {
//                System.out.println("send 2");
//                expriryDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:s")
//                        .parse(this.otpRepository.findOtpByPhoneNumberAndByCode(phoneNumber, randomNumber).getExpiryDate());
//                System.out.println("send 3");
//
//            } catch (ParseException e) {
//                System.out.println("send x");
//                e.printStackTrace();
//            }

            long MAX_DURATION = MILLISECONDS.convert(2, MINUTES);
            long duration = currentDate.getTime() - expriryDate.getTime();

            if (duration <= MAX_DURATION) {
                deleteOtp(this.otpRepository.findOtpByPhoneNumberAndByCode(phoneNumber,randomNumber).getOtpId());
            }
        }
        if (this.otpRepository.findOtpByPhoneNumberAndByCode(phoneNumber,randomNumber) == null) {
            try {
                String uri = "http://10.1.245.150:7080/v1/otp/";

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String code = randomNumber;

                String requestBody = "{\"mobile\":" + "\"" + phoneNumber + "\"," + "\"text\":" + "\""
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
                otp.setPhoneNumber(phoneNumber);
                otp.setCode(code);
                otp.setExpiryDate(dateFormat.format(expirayDate));
                addOtp(otp);
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
    @Override
    public Otp getOtpByCode(String code) {
        return otpRepository.findOtpByCode(code).orElseThrow(
                () -> new ResourceNotFoundException("There is no Otp with this code")
        );
    }

    @Override
    public void deleteOtp(Long otpId) {
        otpRepository.deleteById(otpId);
    }
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
