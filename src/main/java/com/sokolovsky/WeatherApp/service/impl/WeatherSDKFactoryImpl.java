package com.sokolovsky.WeatherApp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sokolovsky.WeatherApp.config.WeatherConfig;
import com.sokolovsky.WeatherApp.service.WeatherSDKFactory;
import com.sokolovsky.WeatherApp.service.WeatherService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WeatherSDKFactoryImpl implements WeatherSDKFactory {
    private final Map<String, WeatherService> instances = new ConcurrentHashMap<>();
    private final WeatherConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherSDKFactoryImpl(WeatherConfig config,
                                 RestTemplate restTemplate,
                                 ObjectMapper objectMapper) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public WeatherService createSDK(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key must not be empty");
        }
        
        return instances.computeIfAbsent(apiKey, 
            k -> new WeatherServiceImpl(restTemplate, objectMapper, config, k));
    }

    public void destroySDK(String apiKey) {
        WeatherService service = instances.remove(apiKey);
        if (service != null) {
            service.shutdown();
        }
    }
} 