package com.dxvalley.crowdfunding.services;

import com.dxvalley.crowdfunding.dto.PaymentInfoDto;
import com.dxvalley.crowdfunding.models.PaymentInfo;

import java.util.List;

public interface PaymentInfoService {
    List<PaymentInfo> getPaymentInfoByCampaignId(Long campaignId);
    Double totalAmountCollectedOnPlatform();
    Double totalAmountCollectedForCampaign(Long campaignId);
    public List<PaymentInfo> getPaymentInfoByUserId(Long userId);
    PaymentInfo addPaymentInfo(PaymentInfoDto paymentInfoDto);
}
