package com.dxvalley.crowdfunding.payment.paymentGateway;

import com.dxvalley.crowdfunding.exception.customException.ResourceNotFoundException;
import com.dxvalley.crowdfunding.payment.paymentGateway.dto.PaymentGatewayDTO;
import com.dxvalley.crowdfunding.payment.paymentGateway.dto.PaymentGatewayMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentGatewayService {
    private final PaymentGatewayRepository paymentGatewayRepository;

    /**
     * Retrieves all payment gateways and converts them to DTOs.
     *
     * @return a list of PaymentGatewayDTO objects representing the payment gateways
     */
    public List<PaymentGatewayDTO> getAllPaymentGateways() {
        List<PaymentGateway> paymentGateways = paymentGatewayRepository.findAll();
        return paymentGateways.stream()
                .map(PaymentGatewayMapper::toGatewayDTO)
                .toList();
    }

    /**
     * Checks if the payment gateway with the given gateway name is active.
     *
     * @param gatewayName the name of the payment gateway to check
     * @return true if the payment gateway is active, false otherwise
     * @throws ResourceNotFoundException if the payment gateway with the given name is not found
     */
    public boolean isPaymentGatewayActive(String gatewayName) {
        PaymentGateway paymentGateway = paymentGatewayRepository.findByGatewayName(gatewayName).
                orElseThrow(() -> new ResourceNotFoundException("Payment Gateway not found: " + gatewayName));
        return paymentGateway.getIsActive();
    }
}
