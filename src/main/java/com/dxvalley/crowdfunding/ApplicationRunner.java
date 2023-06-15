package com.dxvalley.crowdfunding;

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
    private final PasswordEncoder passwordEncoder;

    private static List<Authority> createAuthorities() {
        Authority campaignAuthority = new Authority("Campaign Authority", "Create, update, and read campaign and campaign-related data");
        return List.of(campaignAuthority);
    }

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

    private Role createUserRole(List<Authority> authorities) {
        Role admin = new Role("USER");
        admin.setAuthorities(authorities);

        return admin;
    }

    private Users createUser(Role role) {
        return Users.builder()
                .username("user@coop.com")
                .password(passwordEncoder.encode("123456"))
                .fullName("John Doe")
                .address("Finfinne")
                .userStatus(UserStatus.ACTIVE)
                .role(role)
                .verified(true)
                .createdAt(LocalDateTime.now().format(dateTimeFormatter))
                .build();
    }
}


