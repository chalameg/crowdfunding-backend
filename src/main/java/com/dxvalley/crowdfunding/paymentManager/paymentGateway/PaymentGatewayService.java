package com.dxvalley.crowdfunding.paymentManager.paymentGateway;

import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentGatewayService {
    private final PaymentGatewayRepository paymentGatewayRepository;

    // Retrieves all payment gateways.
    public List<PaymentGateway> getAllPaymentGateways() {
        List<PaymentGateway> paymentGateways = paymentGatewayRepository.findAll();
        if (paymentGateways.isEmpty())
            throw new ResourceNotFoundException("There is no payment gateway");

        return paymentGateways;
    }

    // Checks if the payment is active.
    public boolean isPaymentGatewayActive(String gatewayName) {
        PaymentGateway paymentGateway = paymentGatewayRepository.findByGatewayName(gatewayName).
                orElseThrow(() -> new ResourceNotFoundException("Payment Gateway not found: " + gatewayName));

        return paymentGateway.getIsActive();
    }
}
