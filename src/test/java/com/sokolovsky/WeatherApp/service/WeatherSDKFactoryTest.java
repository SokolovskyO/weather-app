package com.sokolovsky.WeatherApp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sokolovsky.WeatherApp.config.WeatherConfig;
import com.sokolovsky.WeatherApp.service.impl.WeatherSDKFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WeatherSDKFactoryTest {

    @Mock
    private WeatherConfig config;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private WeatherSDKFactory factory;

    @BeforeEach
    void setUp() {
        factory = new WeatherSDKFactoryImpl(config, restTemplate, objectMapper);
    }

    @Test
    void createSDK_ShouldReturnSameInstance_ForSameKey() {
        WeatherService sdk1 = factory.createSDK("test_key");
        WeatherService sdk2 = factory.createSDK("test_key");

        assertSame(sdk1, sdk2, "Should return same instance for same key");
    }

    @Test
    void createSDK_ShouldReturnDifferentInstances_ForDifferentKeys() {
        WeatherService sdk1 = factory.createSDK("key1");
        WeatherService sdk2 = factory.createSDK("key2");

        assertNotSame(sdk1, sdk2, "Should return different instances for different keys");
    }

    @Test
    void createSDK_ShouldThrowException_ForEmptyKey() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.createSDK(""),
                "Should throw exception for empty key");
    }

    @Test
    void destroySDK_ShouldRemoveInstance() {
        String key = "test_key";
        WeatherService sdk1 = factory.createSDK(key);

        factory.destroySDK(key);
        WeatherService sdk2 = factory.createSDK(key);

        assertNotSame(sdk1, sdk2, "Should create new instance after destroy");
    }
} 