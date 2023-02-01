package com.dxvalley.crowdfunding.models;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Transaction {
    
    private String amount;
    private String transactionDate;
    private String PayerName;
    private String PayerPhoneNumber;
    private String PayerEmail;
    private String paymentMethod;

    //Campaign
    //User
}
