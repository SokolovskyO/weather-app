package com.sokolovsky.WeatherApp.service;

public interface WeatherSDKFactory {
    WeatherService createSDK(String apiKey);
    void destroySDK(String apiKey);
} 