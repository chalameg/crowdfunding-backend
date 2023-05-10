package com.dxvalley.crowdfunding;

import com.dxvalley.crowdfunding.user.UserRepository;
import com.dxvalley.crowdfunding.user.Users;
import com.dxvalley.crowdfunding.user.role.Role;
import com.dxvalley.crowdfunding.user.role.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "database", name = "seed", havingValue = "true")
@Slf4j
public class ApplicationRunner {
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

                log.info("Preloaded roles and user");
            } catch (Exception ex) {
                log.error("Preloading Error: {}", ex.getMessage());
                throw new RuntimeException("Preloading Error ", ex);
            }
        };
    }
}

