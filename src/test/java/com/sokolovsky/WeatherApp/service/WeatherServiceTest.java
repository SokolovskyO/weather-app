package com.sokolovsky.WeatherApp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sokolovsky.WeatherApp.config.WeatherConfig;
import com.sokolovsky.WeatherApp.dto.GeocodingDTO;
import com.sokolovsky.WeatherApp.dto.WeatherDTO;
import com.sokolovsky.WeatherApp.exception.WeatherApiException;
import com.sokolovsky.WeatherApp.service.impl.WeatherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock(lenient = true)
    private RestTemplate restTemplate;

    @Mock
    private WeatherConfig config;

    @Mock(lenient = true)
    private ObjectMapper objectMapper;

    private WeatherService weatherService;

    @BeforeEach
    void setUp() throws Exception {
        when(config.getMode()).thenReturn(WeatherConfig.Mode.ON_DEMAND);
        weatherService = new WeatherServiceImpl(restTemplate, objectMapper, config, "test_key");

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn("[{\"lat\":51.5074,\"lon\":-0.1278}]", "{\"main\":{\"temp\":20.0}}");
    }

    @Test
    void getWeatherData_ShouldReturnWeatherData() throws Exception {
        String city = "London";
        String geoResponse = "[{\"lat\":51.5074,\"lon\":-0.1278}]";
        String weatherResponse = "{\"main\":{\"temp\":20.0}}";

        GeocodingDTO geocodingDTO = new GeocodingDTO();
        geocodingDTO.setLat(new BigDecimal("51.5074"));
        geocodingDTO.setLon(new BigDecimal("-0.1278"));

        WeatherDTO weatherDTO = new WeatherDTO();
        WeatherDTO.Main main = new WeatherDTO.Main();
        main.setTemp(20.0);
        weatherDTO.setMain(main);

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(geoResponse)
                .thenReturn(weatherResponse);

        doReturn(List.of(geocodingDTO))
                .when(objectMapper)
                .readValue(anyString(), any(TypeReference.class));

        doReturn(weatherDTO)
                .when(objectMapper)
                .readValue(anyString(), eq(WeatherDTO.class));

        WeatherDTO result = weatherService.getWeatherData(city);

        assertNotNull(result);
        assertEquals(20.0, result.getMain().getTemp());
    }

    @Test
    void getWeatherData_ShouldUseCacheForRepeatedRequests() throws Exception {
        String city = "London";
        String geoResponse = "[{\"lat\":51.5074,\"lon\":-0.1278}]";
        String weatherResponse = "{\"main\":{\"temp\":20.0}}";

        GeocodingDTO geocodingDTO = new GeocodingDTO();
        geocodingDTO.setLat(new BigDecimal("51.5074"));
        geocodingDTO.setLon(new BigDecimal("-0.1278"));

        WeatherDTO weatherDTO = new WeatherDTO();
        WeatherDTO.Main main = new WeatherDTO.Main();
        main.setTemp(20.0);
        weatherDTO.setMain(main);

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(geoResponse)
                .thenReturn(weatherResponse);

        doReturn(List.of(geocodingDTO))
                .when(objectMapper)
                .readValue(anyString(), any(TypeReference.class));

        doReturn(weatherDTO)
                .when(objectMapper)
                .readValue(anyString(), eq(WeatherDTO.class));

        WeatherDTO result1 = weatherService.getWeatherData(city);
        WeatherDTO result2 = weatherService.getWeatherData(city);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.getMain().getTemp(), result2.getMain().getTemp());
    }

    @Test
    void cache_ShouldNotExceedMaxSize() throws Exception {
        GeocodingDTO geocodingDTO = new GeocodingDTO();
        geocodingDTO.setLat(new BigDecimal("51.5074"));
        geocodingDTO.setLon(new BigDecimal("-0.1278"));

        WeatherDTO weatherResponse = new WeatherDTO();
        WeatherDTO.Main main = new WeatherDTO.Main();
        main.setTemp(20.0);
        weatherResponse.setMain(main);

        doReturn(List.of(geocodingDTO))
                .when(objectMapper)
                .readValue(anyString(), any(TypeReference.class));

        doReturn(weatherResponse)
                .when(objectMapper)
                .readValue(anyString(), eq(WeatherDTO.class));

        String[] cities = {
                "London", "Paris", "Berlin", "Madrid", "Rome",
                "Vienna", "Prague", "Warsaw", "Dublin", "Amsterdam",
                "Brussels"
        };

        for (String city : cities) {
            weatherService.getWeatherData(city);
        }

        Thread.sleep(100);
        weatherService.getWeatherData("Stockholm");
        Thread.sleep(100);

        assertTrue(weatherService.getCachedCities().size() <= 10,
                "Cache should not exceed 10 cities");
        assertTrue(weatherService.getCachedCities().contains("Stockholm"),
                "Cache should contain the latest city");
    }

    @Test
    void pollingMode_ShouldStartPollingService() {
        when(config.getMode()).thenReturn(WeatherConfig.Mode.POLLING);

        WeatherService pollingService = new WeatherServiceImpl(  // Используем интерфейс
                restTemplate, objectMapper, config, "test_key");

        assertNotNull(pollingService, "Service should be created in polling mode");
    }

    @Test
    void getWeatherData_ShouldThrowWeatherApiException_WhenCityNotFound() throws Exception {
        String city = "NonExistentCity";
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn("[]");

        assertThrows(WeatherApiException.class,
                () -> weatherService.getWeatherData(city),
                "Should throw WeatherApiException when city not found");
    }
} 