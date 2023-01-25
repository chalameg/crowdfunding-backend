package com.dxvalley.crowdfunding;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.dxvalley.crowdfunding.models.Role;
import com.dxvalley.crowdfunding.models.Users;
import com.dxvalley.crowdfunding.repositories.RoleRepository;
import com.dxvalley.crowdfunding.repositories.UserRepository;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "database", name = "seed", havingValue = "true")
public class Bootstrap {

    Role admin = new Role("admin", "System Administrator");
    Role sysAdmin = new Role("sysAdmin", "Highest Level System Administrator");
    Role user = new Role("user", "crowdfunding Application User");
    Role vetter = new Role("vetter", "funding campaigns vetter");

    Users users = new Users("chalamegersa5@gmail.com", "1234", "Chala Megersa", "", "",  "", "", "", true);
    Collection<Role> roles = new ArrayList<>();

    void setUp () {
        roles.add(admin);
        roles.add(sysAdmin);
        users.setRoles(roles);
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, RoleRepository roleRepository) {
        setUp();
        return args -> {
            log.info("Preloading " + roleRepository.save(admin));
            log.info("Preloading " + roleRepository.save(sysAdmin));
            log.info("Preloading " + userRepository.save(users));
            log.info("Preloading " + roleRepository.save(vetter));
            log.info("Preloading " + roleRepository.save(user));
        };
    }
}