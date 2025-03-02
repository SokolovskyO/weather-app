package com.sokolovsky.WeatherApp.service;

import com.sokolovsky.WeatherApp.dto.WeatherDTO;

import java.util.Set;

public interface WeatherService {
    WeatherDTO getWeatherData(String city);
    void shutdown();
    Set<String> getCachedCities();
    void updateWeatherData(String city);
}
