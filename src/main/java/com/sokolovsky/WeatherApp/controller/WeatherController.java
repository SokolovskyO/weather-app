package com.sokolovsky.WeatherApp.controller;

import com.sokolovsky.WeatherApp.dto.WeatherDTO;
import com.sokolovsky.WeatherApp.exception.WeatherApiException;
import com.sokolovsky.WeatherApp.service.WeatherSDKFactory;
import com.sokolovsky.WeatherApp.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for weather data operations.
 * Provides endpoints to retrieve weather information for cities.
 */
@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherSDKFactory sdkFactory;
    private final String apiKey;
    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);

    /**
     * Creates a new WeatherController with the specified SDK factory and API key.
     *
     * @param sdkFactory Factory for creating WeatherService instances
     * @param apiKey OpenWeather API key for authentication
     */
    public WeatherController(WeatherSDKFactory sdkFactory, String apiKey) {
        this.sdkFactory = sdkFactory;
        this.apiKey = apiKey;
    }

    /**
     * Retrieves current weather data for a specified city.
     *
     * @param city Name of the city to get weather for
     * @return ResponseEntity containing weather data or error status
     * @throws WeatherApiException if city not found or API error occurs
     */
    @GetMapping
    public ResponseEntity<WeatherDTO> getWeather(@RequestParam String city) {
        try {
            WeatherService service = sdkFactory.createSDK(apiKey);
            WeatherDTO result = service.getWeatherData(city);
            return ResponseEntity.ok(result);
        } catch (WeatherApiException e) {
            log.error("Error getting weather data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }
}
