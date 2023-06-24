package com.dxvalley.crowdfunding;

import com.dxvalley.crowdfunding.campaign.campaignFundingType.FundingType;
import com.dxvalley.crowdfunding.campaign.campaignFundingType.FundingTypeRepository;
import com.dxvalley.crowdfunding.campaign.campaignSetting.Setting;
import com.dxvalley.crowdfunding.campaign.campaignSetting.SettingRepository;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentGateway;
import com.dxvalley.crowdfunding.paymentManager.paymentGateway.PaymentGatewayRepository;
import com.dxvalley.crowdfunding.userManager.authority.Authority;
import com.dxvalley.crowdfunding.userManager.authority.AuthorityRepository;
import com.dxvalley.crowdfunding.userManager.role.Role;
import com.dxvalley.crowdfunding.userManager.role.RoleRepository;
import com.dxvalley.crowdfunding.userManager.user.UserRepository;
import com.dxvalley.crowdfunding.userManager.user.UserStatus;
import com.dxvalley.crowdfunding.userManager.user.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "database", name = "seed", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class ApplicationRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DateTimeFormatter dateTimeFormatter;
    private final AuthorityRepository authorityRepository;
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final SettingRepository settingRepository;
    private final PasswordEncoder passwordEncoder;
    private final FundingTypeRepository fundingTypeRepository;


    /**
     * Initializes the database with preloaded data upon application startup.
     */
    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            try {
                // Create and Save authorities
                List<Authority> authorities = authorityRepository.saveAll(createAuthorities());

                // Create and save roles
                Role userRole = createUserRole(authorities);
                Role role = roleRepository.save(userRole);

                // Create and save user
                Users johnDoe = createUser(role);
                userRepository.save(johnDoe);

                log.info("ApplicationRunner => Preloaded authority, roles, and user");
            } catch (Exception ex) {
                log.error("ApplicationRunner Preloading Error: {}", ex.getMessage());
                throw new RuntimeException("ApplicationRunner Preloading Error ", ex);
            }
        };
    }

    private static List<Authority> createAuthorities() {
        Authority campaignAuthority = new Authority("Campaign Authority", "Create, update, and read campaign and campaign-related data");
        return List.of(campaignAuthority);
    }

    private Role createUserRole(List<Authority> authorities) {
        Role admin = new Role("USER");
        admin.setAuthorities(authorities);

        return admin;
    }

    private Users createUser(Role role) {
        return Users.builder()
                .username("user@coop.com")
                .password(this.passwordEncoder.encode("123456"))
                .fullName("John Doe").address("Finfinne")
                .email("user@coop.com").userStatus(UserStatus.ACTIVE)
                .role(role)
                .verified(true)
                .createdAt(LocalDateTime.now().format(this.dateTimeFormatter))
                .build();
    }


    private List<PaymentGateway> createPaymentGateway() {
        List<String> gatewayNames = List.of("CHAPA", "EBIRR", "COOPASS", "PAYPAL", "STRIPE");
        List<PaymentGateway> paymentGateways = new ArrayList();
        Iterator var3 = gatewayNames.iterator();

        while (var3.hasNext()) {
            String gatewayName = (String) var3.next();
            PaymentGateway paymentGateway = new PaymentGateway();
            paymentGateway.setGatewayName(gatewayName);
            paymentGateway.setIsActive(true);
            paymentGateway.setUpdatedAt(LocalDateTime.now().format(this.dateTimeFormatter));
            paymentGateway.setUpdatedBy("System");
            paymentGateways.add(paymentGateway);
        }

        return paymentGateways;
    }

    private Setting createCampaignSetting() {
        Setting setting = new Setting();
        setting.setMaxCommissionForEquity(5.0);
        setting.setMinCommissionForEquity(0.0);
        setting.setMaxCommissionForDonations(3.0);
        setting.setMinCommissionForDonations(0.0);
        setting.setMaxCommissionForRewards(7.5);
        setting.setMinCommissionForRewards(0.0);
        setting.setMaxCampaignGoal(100000.0);
        setting.setMinCampaignGoal(1000.0);
        setting.setMaxCampaignDuration(60);
        setting.setMinCampaignDuration(7);
        setting.setMinDonationAmount(1.0);
        setting.setUpdatedAt(LocalDateTime.now().format(this.dateTimeFormatter));
        setting.setUpdatedBy("System StartUp");
        return setting;
    }

    private List<FundingType> createFundingType() {
        return List.of(new FundingType("Equity"), new FundingType("Reward"), new FundingType("Donation"));
    }
}


