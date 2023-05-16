package com.dxvalley.crowdfunding.admin.paymentGateway;

import com.dxvalley.crowdfunding.exception.ResourceNotFoundException;
import com.dxvalley.crowdfunding.payment.paymentGateway.PaymentGateway;
import com.dxvalley.crowdfunding.payment.paymentGateway.PaymentGatewayRepository;
import com.dxvalley.crowdfunding.payment.paymentGateway.dto.PaymentGatewayDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPaymentGatewayService {
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final DateTimeFormatter dateTimeFormatter;

    /**
     * Retrieves all payment gateways.
     *
     * @return a list of PaymentGateway objects representing the payment gateways
     */
    public List<PaymentGateway> getAllPaymentGateways() {
        try {
            return paymentGatewayRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        } catch (DataAccessException ex) {
            logError("getAllPaymentGateways", ex);
            throw new RuntimeException("Error occurred while accessing the database.");
        }
    }

    /**
     * Updates the status of a payment gateway.
     *
     * @param paymentGatewayDTO The payment gateway object containing the updated status.
     * @return The updated payment gateway object.
     * @throws ResourceNotFoundException if the payment gateway is not found.
     * @throws RuntimeException          if an error occurs while accessing the database.
     */
    @Transactional
    public PaymentGateway setPaymentGatewayStatus(PaymentGatewayDTO paymentGatewayDTO) {
        try {
            PaymentGateway paymentGateway = paymentGatewayRepository.findById(paymentGatewayDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment Gateway not found: " + paymentGatewayDTO.getId()));

            paymentGateway.setIsActive(paymentGatewayDTO.getIsActive());
            paymentGateway.setUpdatedAt(LocalDateTime.now().format(dateTimeFormatter));
            paymentGateway.setUpdatedBy(paymentGatewayDTO.getUpdatedBy());

            return paymentGatewayRepository.save(paymentGateway);
        } catch (DataAccessException ex) {
            logError("setPaymentGatewayStatus", ex);
            throw new RuntimeException("Error occurred while accessing the database.");
        }
    }

    private void logError(String methodName, DataAccessException ex) {
        log.error("An error occurred in {}.{} while accessing the database. Details: {}",
                getClass().getSimpleName(),
                methodName,
                ex.getMessage());
    }
}
