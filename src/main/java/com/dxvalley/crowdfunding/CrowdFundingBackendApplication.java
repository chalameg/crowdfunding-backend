package com.dxvalley.crowdfunding;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cloudinary.Cloudinary;

@SpringBootApplication
public class CrowdFundingBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrowdFundingBackendApplication.class, args);
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

