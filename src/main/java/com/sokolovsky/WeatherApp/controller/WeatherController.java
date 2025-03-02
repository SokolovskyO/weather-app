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

@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherSDKFactory sdkFactory;
    private final String apiKey;
    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);

    public WeatherController(WeatherSDKFactory sdkFactory, String apiKey) {
        this.sdkFactory = sdkFactory;
        this.apiKey = apiKey;
    }

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
