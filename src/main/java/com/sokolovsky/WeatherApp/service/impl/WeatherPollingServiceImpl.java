package com.sokolovsky.WeatherApp.service.impl;

import com.sokolovsky.WeatherApp.service.WeatherPollingService;
import com.sokolovsky.WeatherApp.service.WeatherService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Set;

@Slf4j
public class WeatherPollingServiceImpl implements WeatherPollingService {
    private final WeatherService weatherService;
    private final ScheduledExecutorService scheduler;
    private static final int POLLING_INTERVAL = 10; // minutes

    public WeatherPollingServiceImpl(WeatherService weatherService) {
        this.weatherService = weatherService;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::updateWeatherData, 
            0, POLLING_INTERVAL, TimeUnit.MINUTES);
        log.info("Weather polling service started");
    }

    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("Weather polling service stopped");
    }

    private void updateWeatherData() {
        try {
            Set<String> cities = weatherService.getCachedCities();
            if (cities.isEmpty()) {
                log.debug("No cities in cache to update");
                return;
            }

            log.debug("Starting weather update for {} cached cities", cities.size());
            for (String city : cities) {
                try {
                    weatherService.updateWeatherData(city);
                    log.debug("Successfully updated weather data for city: {}", city);
                } catch (Exception e) {
                    log.error("Failed to update weather data for city: {}", city, e);
                }
            }
            log.debug("Completed weather update for all cached cities");
        } catch (Exception e) {
            log.error("Error during weather data update", e);
        }
    }
} 