package com.dxvalley.crowdfunding;

import com.dxvalley.crowdfunding.model.Role;
import com.dxvalley.crowdfunding.model.Users;
import com.dxvalley.crowdfunding.repository.RoleRepository;
import com.dxvalley.crowdfunding.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "database", name = "seed", havingValue = "true")
public class ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRunner.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DateTimeFormatter dateTimeFormatter;

    public ApplicationRunner(UserRepository userRepository, RoleRepository roleRepository, DateTimeFormatter dateTimeFormatter) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            try {
                Role admin = new Role("Admin", "System Administrator");
                Role sysAdmin = new Role("SuperAdmin", "Highest Level System Administrator");
                Role user = new Role("User", "Application user");
                Role vetter = new Role("Vetter", "Campaigns vetter");

                Users johnDoe = new Users(
                        "johnDoe@gmail.com",
                        "12345678",
                        "john Doe",
                        "john.com",
                        "doe bio",
                        LocalDateTime.now().format(dateTimeFormatter),
                        "",
                        true,
                        "Finfinne");

                List<Role> roles = roleRepository.saveAll(List.of(admin, sysAdmin, user, vetter));

                johnDoe.setRoles(roles);
                userRepository.save(johnDoe);

                LOGGER.info("Preloaded roles and user");
            } catch (Exception ex) {
                LOGGER.error("Preloading Error: {}", ex.getMessage());
                throw new RuntimeException("Preloading Error ", ex);
            }
        };
    }
}

