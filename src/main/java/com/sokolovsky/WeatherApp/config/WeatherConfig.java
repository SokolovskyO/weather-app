package com.sokolovsky.WeatherApp.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class WeatherConfig {
    @Value("${weather.mode:ON_DEMAND}")
    private Mode mode;

    @Value("${weather.geo-url}")
    private String geoUrl;

    @Value("${weather.weather-url}")
    private String weatherUrl;

    public enum Mode {
        ON_DEMAND,
        POLLING
    }
} 