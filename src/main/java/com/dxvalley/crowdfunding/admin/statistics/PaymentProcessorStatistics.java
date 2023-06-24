package com.dxvalley.crowdfunding.admin.statistics;

import lombok.Data;

@Data
public class PaymentProcessorStatistics {
    private String name;
    private int usageCount;
    private String usagePercentage;
    private double amountCollected;
    private String amountCollectedPercentage;

}
