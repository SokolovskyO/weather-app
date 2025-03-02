package com.sokolovsky.WeatherApp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sokolovsky.WeatherApp.config.WeatherConfig;
import com.sokolovsky.WeatherApp.service.WeatherSDKFactory;
import com.sokolovsky.WeatherApp.service.WeatherService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory implementation for creating and managing Weather Service instances.
 * Maintains a cache of service instances per API key.
 */
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

    /**
     * Creates a new Weather Service instance or returns existing one for the given API key.
     *
     * @param apiKey OpenWeather API key
     * @return Weather Service instance
     * @throws IllegalArgumentException if API key is null or empty
     */
    public WeatherService createSDK(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key must not be empty");
        }
        
        return instances.computeIfAbsent(apiKey, 
            k -> new WeatherServiceImpl(restTemplate, objectMapper, config, k));
    }

    /**
     * Destroys the Weather Service instance for the given API key.
     * Cleans up resources and removes from cache.
     *
     * @param apiKey API key of the service to destroy
     */
    public void destroySDK(String apiKey) {
        WeatherService service = instances.remove(apiKey);
        if (service != null) {
            service.shutdown();
        }
    }
} 