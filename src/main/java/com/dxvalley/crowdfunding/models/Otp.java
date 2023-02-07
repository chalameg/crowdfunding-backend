package com.dxvalley.crowdfunding.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class Otp {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long otpId;
    private String phoneNumber;
    private String code;
    @JsonFormat(pattern="yyyy-MM-dd hh:mm:s",shape = Shape.STRING)
    @Column(name="otp_Expire_At")
    private String expiryDate;

    public Otp(String phoneNumber, String code) {
        this.phoneNumber = phoneNumber;
        this.code = code;
    }
}

