package com.dxvalley.crowdfunding.admin.statistics;

import lombok.Data;

import java.util.List;

@Data
public class PaymentStatistics {
    private Double totalAmountCollectedOnPlatFormInBirr;
    private Double totalCollectedInDollar;
    private Double totalCollectedInBirr;
    private List<PaymentProcessorStatistics> paymentProcessors;

}
