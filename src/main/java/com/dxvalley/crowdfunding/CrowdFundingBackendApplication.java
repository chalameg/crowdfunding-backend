package com.dxvalley.crowdfunding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication()
@EnableAsync(proxyTargetClass = true)
@EnableCaching
public class CrowdFundingBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrowdFundingBackendApplication.class, args);
    }
}

