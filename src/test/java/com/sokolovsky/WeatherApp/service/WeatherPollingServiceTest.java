package com.sokolovsky.WeatherApp.service;

import com.sokolovsky.WeatherApp.service.impl.WeatherPollingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherPollingServiceTest {

    @Mock
    private WeatherService weatherService;

    @Test
    void updateWeatherData_ShouldUpdateAllCachedCities() {
        WeatherPollingService pollingService = new WeatherPollingServiceImpl(weatherService);
        when(weatherService.getCachedCities()).thenReturn(Set.of("London", "Paris", "Berlin"));

        pollingService.start();

        verify(weatherService, timeout(1000)).updateWeatherData("London");
        verify(weatherService, timeout(1000)).updateWeatherData("Paris");
        verify(weatherService, timeout(1000)).updateWeatherData("Berlin");
    }

    @Test
    void stop_ShouldStopUpdating() throws InterruptedException {
        WeatherPollingService pollingService = new WeatherPollingServiceImpl(weatherService);
        when(weatherService.getCachedCities()).thenReturn(Set.of("London"));

        pollingService.start();
        Thread.sleep(100);
        pollingService.stop();

        verify(weatherService, atMost(1)).updateWeatherData(anyString());
    }
} 