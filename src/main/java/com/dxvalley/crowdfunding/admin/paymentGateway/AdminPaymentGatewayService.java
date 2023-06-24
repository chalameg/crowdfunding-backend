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

    public List<PaymentGateway> getAllPaymentGateways() {
        return this.paymentGatewayRepository.findAll(Sort.by(Sort.Direction.ASC, new String[]{"id"}));
    }

    @Transactional
    public PaymentGateway setPaymentGatewayStatus(PaymentGatewayReq paymentGatewayReq) {
        PaymentGateway paymentGateway = (PaymentGateway)this.paymentGatewayRepository.findByGatewayName(paymentGatewayReq.getGatewayName()).orElseThrow(() -> {
            return new ResourceNotFoundException("Payment Gateway not found: " + paymentGatewayReq.getGatewayName());
        });
        paymentGateway.setIsActive(paymentGatewayReq.getIsActive());
        paymentGateway.setUpdatedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        paymentGateway.setUpdatedBy(paymentGatewayReq.getUpdatedBy());
        return (PaymentGateway)this.paymentGatewayRepository.save(paymentGateway);
    }

}
