//package com.dxvalley.crowdfunding.payment.paymentDTO;
//
//import com.dxvalley.crowdfunding.payment.Payment;
//import com.dxvalley.crowdfunding.payment.PaymentProcessor;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@Service
//@RequiredArgsConstructor
//public class PaymentDTOMapper {
//    private final DateTimeFormatter dateTimeFormatter;
//
//    public Payment chapaToPaymentModel(ChapaRequestDTO chapaRequestDTO) {
//        Payment payment = new Payment();
//
//        payment.setPayerFullName(chapaRequestDTO.getFirst_name() + " " + chapaRequestDTO.getLast_name());
//        payment.setTransactionOrderedDate(LocalDateTime.now().format(dateTimeFormatter));
//        payment.setPaymentStatus("PENDING");
//        payment.setIsAnonymous(chapaRequestDTO.getIsAnonymous());
//        payment.setAmount(chapaRequestDTO.getAmount());
//        payment.setCurrency("ETB");
//        payment.setPaymentProcessor(PaymentProcessor.CHAPA);
//
//        return payment;
//    }
//}
