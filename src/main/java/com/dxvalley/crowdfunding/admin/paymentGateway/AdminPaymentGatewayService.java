package com.dxvalley.crowdfunding.admin.paymentGateway;

import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentGateway;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentGatewayRepository;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.dto.PaymentGatewayReq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminPaymentGatewayService {
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final DateTimeFormatter dateTimeFormatter;

    // Retrieves all payment gateways.
    public List<PaymentGateway> getAllPaymentGateways() {
        return paymentGatewayRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

    }

    // Updates the status of a payment gateway.
    @Transactional
    public PaymentGateway setPaymentGatewayStatus(PaymentGatewayReq paymentGatewayReq) {
        PaymentGateway paymentGateway = paymentGatewayRepository.findByGatewayName(paymentGatewayReq.getGatewayName())
                .orElseThrow(() -> new ResourceNotFoundException("Payment Gateway not found: " + paymentGatewayReq.getGatewayName()));

        paymentGateway.setIsActive(paymentGatewayReq.getIsActive());
        paymentGateway.setUpdatedAt(LocalDateTime.now().format(dateTimeFormatter));
        paymentGateway.setUpdatedBy(paymentGatewayReq.getUpdatedBy());

        return paymentGatewayRepository.save(paymentGateway);
    }

}
