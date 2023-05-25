package com.dxvalley.crowdfunding.configuration;

import com.cloudinary.Cloudinary;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class FileUploadConfig {
    private final CloudinaryProperties cloudinaryProperties;

    @Bean
    public Cloudinary cloudinaryConfig() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", cloudinaryProperties.getCloud_name());
            config.put("api_key", cloudinaryProperties.getApi_key());
            config.put("api_secret", cloudinaryProperties.getApi_secret());
            return new Cloudinary(config);
        } catch (Exception ex) {
            throw new IllegalStateException("Error creating Cloudinary bean.", ex);
        }
    }

    @Component
    @ConfigurationProperties(prefix = "cloudinary")
    @Data
    private static class CloudinaryProperties {
        private String cloud_name;
        private String api_key;
        private String api_secret;
    }
}
