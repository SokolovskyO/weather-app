package com.sokolovsky.WeatherApp.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sokolovsky.WeatherApp.dto.WeatherDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Web configuration class for the Weather App.
 * Configures HTTP client, JSON processing, and response objects.
 */
@Configuration
public class WebConfig {

    /**
     * Creates RestTemplate bean for making HTTP requests to the Weather API.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Creates ObjectMapper configured for Weather API JSON processing.
     * Enables pretty printing and single value array deserialization.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    /**
     * Creates WeatherDTO bean for weather data responses.
     */
    @Bean
    public WeatherDTO weatherDTO() {
        return new WeatherDTO();
    }
} 