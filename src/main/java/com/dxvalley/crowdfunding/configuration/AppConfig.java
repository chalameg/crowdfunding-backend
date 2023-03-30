package com.dxvalley.crowdfunding.configuration;

import com.cloudinary.Cloudinary;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {
    private final String cloudName = "do394twgw";
    private final String apiKey = "563676698242191";
    private final String apiSecret = "fYRKyRcysOZmtWP06cgXRu_Fpq0";

//
//
//    public AppConfig(@Value("${CLOUDINARY_CLOUD_NAME}") String cloudName,
//                     @Value("${CLOUDINARY_API_KEY}") String apiKey,
//                     @Value("${CLOUDINARY_API_SECRET}") String apiSecret) {
//        this.cloudName = cloudName;
//        this.apiKey = apiKey;
//        this.apiSecret = apiSecret;
//    }

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .create();
    }

    @Bean
    public Cloudinary cloudinaryConfig() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", cloudName);
            config.put("api_key", apiKey);
            config.put("api_secret", apiSecret);
            return new Cloudinary(config);
        } catch (Exception ex) {
            throw new IllegalStateException("Error creating Cloudinary bean.", ex);
        }
    }
}