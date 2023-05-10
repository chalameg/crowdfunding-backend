package com.dxvalley.crowdfunding.admin.general;

import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.payment.PaymentProcessor;
import com.dxvalley.crowdfunding.payment.PaymentRepository;
import com.dxvalley.crowdfunding.user.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashBoardService {
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private double totalTimesUsed;
    private double totalAmountCollected;

    public DashBoardResponse getStatistics() {
        DashBoardResponse dashBoardResponse = new DashBoardResponse();
        UserResponse userResponse = new UserResponse();
        CampaignStatisticsResponse campResponse = new CampaignStatisticsResponse();
        PaymentStatisticsResponse paymentResponse = new PaymentStatisticsResponse();

        var campaigns = campaignRepository.findAll().stream()
                .filter(campaign -> !(campaign.getCampaignStage().equals(CampaignStage.INITIAL))).toList();

        int totalCampaigns = campaigns.size();
        int activeCampaigns = (int) campaigns.stream()
                .filter(campaign -> campaign.getCampaignStage().equals(CampaignStage.FUNDING))
                .count();
        int rewardCampaigns = (int) campaigns.stream()
                .filter(campaign -> campaign.getFundingType().getName().equals("REWARD"))
                .count();
        int equityCampaigns = (int) campaigns.stream()
                .filter(campaign -> campaign.getFundingType().getName().equals("EQUITY"))
                .count();
        int donationCampaigns = (int) campaigns.stream()
                .filter(campaign -> campaign.getFundingType().getName().equals("DONATION"))
                .count();

        campResponse.setTotalCampaigns(totalCampaigns);
        campResponse.setTotalActiveCampaigns((activeCampaigns));
        campResponse.setDonationCampaigns(donationCampaigns);
        campResponse.setEquityCampaigns(equityCampaigns);
        campResponse.setRewardsCampaigns(rewardCampaigns);

        var users = userRepository.findAll().stream().count();
        userResponse.setTotalUsers((int) users);


        var payments = paymentRepository.findAll();



        double birr = 0;
        double dollar = 0;
        double totalAmount = 0;
        double cooPassTimesUsed = 0;
        double stripeTimesUsed = 0;
        double chapaTimesUsed = 0;
        double paypalTimesUsed = 0;

        double amountCollectedCooPass = 0;
        double amountCollectedStripe = 0;
        double amountCollectedChapa = 0;
        double amountCollectedPaypal = 0;


        for(var payment: payments){
            if(payment.getPaymentProcessor().equals(PaymentProcessor.COOPASS)){
                cooPassTimesUsed ++;
                amountCollectedCooPass +=payment.getAmount();
            }
            if(payment.getPaymentProcessor().equals(PaymentProcessor.CHAPA)){
                chapaTimesUsed ++;
                amountCollectedChapa += payment.getAmount();
            }
            if(payment.getPaymentProcessor().equals(PaymentProcessor.PAYPAL)){
                paypalTimesUsed ++;
                amountCollectedPaypal += payment.getAmount()*54;
            }
            if(payment.getPaymentProcessor().equals(PaymentProcessor.STRIPE)){
                stripeTimesUsed ++;
                amountCollectedStripe +=payment.getAmount() * 54;
            }

            if (payment.getCurrency().equals("ETB")){
                birr +=payment.getAmount();
                continue;
            }
            dollar += payment.getAmount();
        }

        int totalTimesUsed = payments.size();

        totalAmountCollected = birr + dollar * 54;

        List<PaymentProcessorResponse> paymentProcessorResponses = new ArrayList<>();

        PaymentProcessorResponse chapaPaymentProcessor = paymentProcessorResponse(PaymentProcessor.CHAPA.toString(),chapaTimesUsed,amountCollectedChapa,totalTimesUsed,totalAmountCollected);
        PaymentProcessorResponse cooPassPaymentProcessor = paymentProcessorResponse(PaymentProcessor.COOPASS.toString(),cooPassTimesUsed,amountCollectedCooPass,totalTimesUsed,totalAmountCollected);
        PaymentProcessorResponse payPalPaymentProcessor = paymentProcessorResponse(PaymentProcessor.PAYPAL.toString(),paypalTimesUsed,amountCollectedPaypal,totalTimesUsed,totalAmountCollected);
        PaymentProcessorResponse stripPaymentProcessor = paymentProcessorResponse(PaymentProcessor.STRIPE.toString(),stripeTimesUsed,amountCollectedStripe,totalTimesUsed,totalAmountCollected);

        paymentProcessorResponses.add(cooPassPaymentProcessor);
        paymentProcessorResponses.add(chapaPaymentProcessor);
        paymentProcessorResponses.add(payPalPaymentProcessor);
        paymentProcessorResponses.add(stripPaymentProcessor);

        paymentResponse.setPaymentProcessors(paymentProcessorResponses);

        paymentResponse.setTotalAmountCollectedOnPlatFormInBirr(totalAmount);
        paymentResponse.setTotalCollectedInDollar(dollar);
        paymentResponse.setTotalCollectedInBirr(birr);

        dashBoardResponse.setUserStatistics(userResponse);
        dashBoardResponse.setCampaignStatistics(campResponse);
        dashBoardResponse.setPaymentStatistics(paymentResponse);

        return dashBoardResponse;
    }



    private PaymentProcessorResponse paymentProcessorResponse(String mame,double usageCount, double amountCollected,double totalTimesUsed,double totalAmountCollected){
        PaymentProcessorResponse paymentProcessorResponse = new PaymentProcessorResponse();

        paymentProcessorResponse.setName(mame);
        paymentProcessorResponse.setUsageCount((int) usageCount);
        paymentProcessorResponse.setAmountCollected(amountCollected);
        paymentProcessorResponse.setUsagePercentage(String.format("%.2f", (usageCount/totalTimesUsed)*100));
        paymentProcessorResponse.setAmountCollectedPercentage(String.format("%.2f",(amountCollected/totalAmountCollected)*100));

        return paymentProcessorResponse;
    }


    @Data
     class DashBoardResponse {
        private  UserResponse userStatistics;
        private PaymentStatisticsResponse paymentStatistics;
        private  CampaignStatisticsResponse campaignStatistics;
    }

    @Data
    class CampaignStatisticsResponse{
        private Integer totalCampaigns;
        private Integer totalActiveCampaigns;
        private Integer donationCampaigns;
        private Integer equityCampaigns;
        private Integer rewardsCampaigns;
    }
    @Data
    class UserResponse{
        private int totalUsers;
    }

    @Data
    class PaymentStatisticsResponse{
        private Double totalAmountCollectedOnPlatFormInBirr;
        private Double totalCollectedInDollar;
        private Double totalCollectedInBirr;
        private List<PaymentProcessorResponse> paymentProcessors;
    }

    @Data
    class PaymentProcessorResponse{
        private String name;
        private int usageCount;
        private String usagePercentage;
        private double amountCollected;
        private String amountCollectedPercentage;
    }

}

