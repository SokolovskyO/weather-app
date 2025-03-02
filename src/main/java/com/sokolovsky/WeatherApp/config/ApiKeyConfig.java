package com.sokolovsky.WeatherApp.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ApiKeyConfig {
    @Bean
    public String apiKey(ApplicationArguments args) {
        List<String> values = args.getOptionValues("api.key");
        if (values == null || values.isEmpty()) {
            return "dummy_key_for_tests";
        }
        String apiKey = values.get(0);
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key must be provided. Use --api.key=YOUR_KEY");
        }
        return apiKey;
    }
} 