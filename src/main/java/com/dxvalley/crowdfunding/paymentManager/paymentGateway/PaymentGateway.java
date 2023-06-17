package com.dxvalley.crowdfunding.paymentManager.paymentGateway;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGateway {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String gatewayName;
    private Boolean isActive;
    private String updatedAt;
    private String updatedBy;
}
