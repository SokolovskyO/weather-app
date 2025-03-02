package com.sokolovsky.WeatherApp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sokolovsky.WeatherApp.config.WeatherConfig;
import com.sokolovsky.WeatherApp.dto.GeocodingDTO;
import com.sokolovsky.WeatherApp.dto.WeatherDTO;
import com.sokolovsky.WeatherApp.exception.WeatherApiException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sokolovsky.WeatherApp.service.WeatherPollingService;
import com.sokolovsky.WeatherApp.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Set;

/**
 * Core implementation of the Weather Service with caching and polling capabilities.
 */
@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final WeatherConfig config;
    private final String apiKey;
    private final Cache<String, WeatherDTO> cache;
    private final WeatherPollingService pollingService;

    public WeatherServiceImpl(RestTemplate restTemplate,
                              ObjectMapper objectMapper,
                              WeatherConfig config,
                              String apiKey) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.config = config;
        this.apiKey = apiKey;
        this.cache = Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener((key, value, cause) -> 
                    log.debug("Removed from cache: {} due to {}", key, cause))
                .evictionListener((key, value, cause) -> 
                    log.debug("Evicted from cache: {} due to {}", key, cause))
                .build();
                
        if (config.getMode() == WeatherConfig.Mode.POLLING) {
            this.pollingService = new WeatherPollingServiceImpl(this);
            this.pollingService.start();
        } else {
            this.pollingService = null;
        }
    }

    @PostConstruct
    public void validateApiKey() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("API key must be provided");
        }
        log.info("Weather service initialized with API key");
    }

    /**
     * Gracefully shuts down the service and clears cache.
     * If polling is enabled, stops the polling service.
     */
    public void shutdown() {
        if (pollingService != null) {
            pollingService.stop();
        }
        cache.invalidateAll();
    }

    /**
     * Gets weather data for a specified city.
     * First checks the cache, if not found or expired, fetches from API.
     *
     * @param city Name of the city
     * @return Weather data for the city
     * @throws WeatherApiException if city not found or API error occurs
     */
    public WeatherDTO getWeatherData(String city) {
        String trimmedCity = city.trim();

        WeatherDTO cached = cache.getIfPresent(trimmedCity);
        if (cached != null) {
            log.debug("Cache hit for city: {}", trimmedCity);
            return cached;
        }

        return fetchWeatherData(trimmedCity);
    }

    private WeatherDTO fetchWeatherData(String city) {
        try {
            String geoUrl = String.format("%s?q=%s&limit=1&appid=%s", 
                config.getGeoUrl(), city, apiKey);
            String geoResponse = restTemplate.getForObject(geoUrl, String.class);
            log.debug("Geo API response: {}", geoResponse);

            if (geoResponse == null || geoResponse.equals("[]")) {
                throw new WeatherApiException("City not found: " + city);
            }

            GeocodingDTO location = objectMapper.readValue(geoResponse,
                new TypeReference<List<GeocodingDTO>>() {})
                .stream()
                .findFirst()
                .orElseThrow(() -> new WeatherApiException("Failed to parse location data for city: " + city));

            String weatherUrl = String.format("%s?lat=%s&lon=%s&appid=%s&units=metric", 
                config.getWeatherUrl(), location.getLat(), location.getLon(), apiKey);
            
            try {
                String weatherResponse = restTemplate.getForObject(weatherUrl, String.class);
                if (weatherResponse == null) {
                    throw new WeatherApiException("Failed to get weather data for city: " + city);
                }
                log.debug("Weather API response: {}", weatherResponse);

                WeatherDTO weatherDTO = objectMapper.readValue(weatherResponse, WeatherDTO.class);
                cache.put(city, weatherDTO);
                return weatherDTO;
            } catch (RestClientException e) {
                throw new WeatherApiException("Error accessing weather API: " + e.getMessage(), e);
            }
        } catch (JsonProcessingException e) {
            throw new WeatherApiException("Failed to parse weather data", e);
        } catch (RestClientException e) {
            throw new WeatherApiException("Error accessing geo API: " + e.getMessage(), e);
        }
    }

    /**
     * Returns a set of city names currently stored in cache.
     *
     * @return Set of cached city names
     */
    public Set<String> getCachedCities() {
        return cache.asMap().keySet();
    }

    /**
     * Forces an update of weather data for a specific city.
     * Bypasses cache and fetches fresh data from API.
     *
     * @param city Name of the city to update
     * @throws WeatherApiException if update fails
     */
    public void updateWeatherData(String city) {
        fetchWeatherData(city);
    }
}

