package com.dxvalley.crowdfunding.security.configuration;

import com.dxvalley.crowdfunding.security.jwt.JwtAuthenticationFilter;
import com.dxvalley.crowdfunding.security.jwt.JwtAuthorizationFilter;
import com.dxvalley.crowdfunding.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/**").permitAll();
//                    auth.requestMatchers("/login").permitAll();
//                    auth.requestMatchers("/admin/admin-users/**")
//                            .hasAnyAuthority("Dashboard", "Settings", "Jigii User Management", "Admin User Management", "Campaign Management");
//
//                    auth.requestMatchers(HttpMethod.POST, "/admin/admin-users/**").hasAuthority("Admin User Management");
//                    auth.requestMatchers(HttpMethod.DELETE, "/admin/admin-users/**").hasAuthority("Admin User Management");
//                    auth.anyRequest().authenticated();
                })
                .addFilter(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration)))
                .addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}