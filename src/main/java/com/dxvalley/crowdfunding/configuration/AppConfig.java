package com.dxvalley.crowdfunding.configuration;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {
    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Cloudinary cloudinaryConfig() {
        Cloudinary cloudinary = null;
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "do394twgw");
        config.put("api_key", "563676698242191");
        config.put("api_secret", "fYRKyRcysOZmtWP06cgXRu_Fpq0");
        cloudinary = new Cloudinary(config);
        return cloudinary;
    }
}