package com.dxvalley.crowdfunding.admin.statistics;

import com.dxvalley.crowdfunding.campaign.campaign.Campaign;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignRepository;
import com.dxvalley.crowdfunding.campaign.campaign.CampaignStage;
import com.dxvalley.crowdfunding.paymentManager.payment.Payment;
import com.dxvalley.crowdfunding.paymentManager.payment.PaymentRepository;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentProcessor;
import com.dxvalley.crowdfunding.userManager.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class StatisticsService {
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private double totalAmountCollected;

    public StatisticsData getStatistics() {
        StatisticsData dashBoardResponse = new StatisticsData();
        UserStatistics userResponse = new UserStatistics();
        CampaignStatistics campResponse = new CampaignStatistics();
        PaymentStatistics paymentResponse = new PaymentStatistics();
        List<Campaign> campaigns = this.campaignRepository.findAll().stream().filter((campaign) -> {
            return !campaign.getCampaignStage().equals(CampaignStage.INITIAL);
        }).toList();
        int totalCampaigns = campaigns.size();
        int activeCampaigns = (int)campaigns.stream().filter((campaign) -> {
            return campaign.getCampaignStage().equals(CampaignStage.FUNDING);
        }).count();
        int rewardCampaigns = (int)campaigns.stream().filter((campaign) -> {
            return campaign.getFundingType().getName().equals("REWARD");
        }).count();
        int equityCampaigns = (int)campaigns.stream().filter((campaign) -> {
            return campaign.getFundingType().getName().equals("EQUITY");
        }).count();
        int donationCampaigns = (int)campaigns.stream().filter((campaign) -> {
            return campaign.getFundingType().getName().equals("DONATION");
        }).count();
        campResponse.setTotalCampaigns(totalCampaigns);
        campResponse.setTotalActiveCampaigns(activeCampaigns);
        campResponse.setDonationCampaigns(donationCampaigns);
        campResponse.setEquityCampaigns(equityCampaigns);
        campResponse.setRewardsCampaigns(rewardCampaigns);
        long users = this.userRepository.findAll().stream().count();
        userResponse.setTotalUsers((int)users);
        List<Payment> payments = this.paymentRepository.findAll();
        double birr = 0.0;
        double dollar = 0.0;
        double totalAmount = 0.0;
        double cooPassTimesUsed = 0.0;
        double stripeTimesUsed = 0.0;
        double chapaTimesUsed = 0.0;
        double paypalTimesUsed = 0.0;
        double amountCollectedCooPass = 0.0;
        double amountCollectedStripe = 0.0;
        double amountCollectedChapa = 0.0;
        double amountCollectedPaypal = 0.0;
        Iterator var36 = payments.iterator();

        while(var36.hasNext()) {
            Payment payment = (Payment)var36.next();
            if (payment.getPaymentProcessor().equals(PaymentProcessor.COOPASS)) {
                ++cooPassTimesUsed;
                amountCollectedCooPass += payment.getAmount();
            }

            if (payment.getPaymentProcessor().equals(PaymentProcessor.CHAPA)) {
                ++chapaTimesUsed;
                amountCollectedChapa += payment.getAmount();
            }

            if (payment.getPaymentProcessor().equals(PaymentProcessor.PAYPAL)) {
                ++paypalTimesUsed;
                amountCollectedPaypal += payment.getAmount() * 54.0;
            }

            if (payment.getPaymentProcessor().equals(PaymentProcessor.STRIPE)) {
                ++stripeTimesUsed;
                amountCollectedStripe += payment.getAmount() * 54.0;
            }

            if (payment.getCurrency().equals("ETB")) {
                birr += payment.getAmount();
            } else {
                dollar += payment.getAmount();
            }
        }

        int totalTimesUsed = payments.size();
        this.totalAmountCollected = birr + dollar * 54.0;
        List<PaymentProcessorStatistics> paymentProcessorResponses = new ArrayList();
        PaymentProcessorStatistics chapaPaymentProcessor = this.paymentProcessorResponse(PaymentProcessor.CHAPA.toString(), chapaTimesUsed, amountCollectedChapa, (double)totalTimesUsed, this.totalAmountCollected);
        PaymentProcessorStatistics cooPassPaymentProcessor = this.paymentProcessorResponse(PaymentProcessor.COOPASS.toString(), cooPassTimesUsed, amountCollectedCooPass, (double)totalTimesUsed, this.totalAmountCollected);
        PaymentProcessorStatistics payPalPaymentProcessor = this.paymentProcessorResponse(PaymentProcessor.PAYPAL.toString(), paypalTimesUsed, amountCollectedPaypal, (double)totalTimesUsed, this.totalAmountCollected);
        PaymentProcessorStatistics stripPaymentProcessor = this.paymentProcessorResponse(PaymentProcessor.STRIPE.toString(), stripeTimesUsed, amountCollectedStripe, (double)totalTimesUsed, this.totalAmountCollected);
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

    private PaymentProcessorStatistics paymentProcessorResponse(String mame, double usageCount, double amountCollected, double totalTimesUsed, double totalAmountCollected) {
        PaymentProcessorStatistics paymentProcessorResponse = new PaymentProcessorStatistics();
        paymentProcessorResponse.setName(mame);
        paymentProcessorResponse.setUsageCount((int)usageCount);
        paymentProcessorResponse.setAmountCollected(amountCollected);
        paymentProcessorResponse.setUsagePercentage(String.format("%.2f", usageCount / totalTimesUsed * 100.0));
        paymentProcessorResponse.setAmountCollectedPercentage(String.format("%.2f", amountCollected / totalAmountCollected * 100.0));
        return paymentProcessorResponse;
    }

    public StatisticsService(final CampaignRepository campaignRepository, final UserRepository userRepository, final PaymentRepository paymentRepository) {
        this.campaignRepository = campaignRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }
}
