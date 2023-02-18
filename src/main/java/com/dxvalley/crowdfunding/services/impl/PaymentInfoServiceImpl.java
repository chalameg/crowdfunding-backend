package com.dxvalley.crowdfunding.services.impl;

import com.dxvalley.crowdfunding.dto.PaymentInfoDto;
import com.dxvalley.crowdfunding.exceptions.ResourceNotFoundException;
import com.dxvalley.crowdfunding.models.Campaign;
import com.dxvalley.crowdfunding.models.PaymentInfo;
import com.dxvalley.crowdfunding.repositories.CampaignRepository;
import com.dxvalley.crowdfunding.repositories.PaymentInfoRepository;
import com.dxvalley.crowdfunding.services.CampaignService;
import com.dxvalley.crowdfunding.services.PaymentInfoService;
import com.dxvalley.crowdfunding.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentInfoServiceImpl implements PaymentInfoService {
    @Autowired
    PaymentInfoRepository paymentInfoRepository;
    @Autowired
    UserService userService;
    @Autowired
    CampaignRepository campaignRepository;
    @Override
    public List<PaymentInfo> getPaymentInfoByCampaignId(Long campaignId) {
        var paymentInfo = paymentInfoRepository.findPaymentInfoByCampaignId(campaignId);
        if (paymentInfo.size() == 0){
            throw new ResourceNotFoundException("Currently, there is no Payment for this Campaign.");
        }
        return paymentInfo;
    }

    @Override
    public Double totalAmountCollectedOnPlatform() {
        return paymentInfoRepository.findTotalAmountRaisedOnPlatform();
    }

    @Override
    public Double totalAmountCollectedForCampaign(Long campaignId) {
        return paymentInfoRepository.findTotalAmountOfPaymentForCampaign(campaignId);
    }

    @Override
    public List<PaymentInfo> getPaymentInfoByUserId(Long userId) {
        var paymentInfo = paymentInfoRepository.findPaymentInfoByUserId(userId);
        if (paymentInfo.size() == 0){
            throw new ResourceNotFoundException("You have not contributed yet.");
        }
        return paymentInfo;
    }

    @Override
    public PaymentInfo addPaymentInfo(PaymentInfoDto paymentInfoDto) {
        PaymentInfo paymentInfo = new PaymentInfo();
        var user = userService.getUserById(paymentInfoDto.getUserId());
        LocalDateTime paidAt = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Campaign campaign = campaignRepository.findCampaignByCampaignId(paymentInfoDto.getCampaignId()).orElseThrow(
                () -> new ResourceNotFoundException("There is no campaign with this ID.")
        );

        paymentInfo.setUser(user);
        paymentInfo.setCampaign(campaign);
        paymentInfo.setAmount(paymentInfoDto.getAmount());
        paymentInfo.setPaidAt(paidAt.format(dateTimeFormatter));

        return paymentInfoRepository.save(paymentInfo);
    }

}
