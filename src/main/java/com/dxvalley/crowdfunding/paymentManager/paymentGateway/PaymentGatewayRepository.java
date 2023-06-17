package com.dxvalley.crowdfunding.paymentManager.paymentGateway;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentGatewayRepository extends JpaRepository<PaymentGateway, Long> {
    Optional<PaymentGateway> findByGatewayName(String gatewayName);
}