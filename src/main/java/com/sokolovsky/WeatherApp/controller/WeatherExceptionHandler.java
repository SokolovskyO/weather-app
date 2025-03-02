package com.sokolovsky.WeatherApp.controller;

import com.sokolovsky.WeatherApp.exception.WeatherApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for Weather API endpoints.
 * Provides consistent error responses across the application.
 */
@ControllerAdvice
public class WeatherExceptionHandler {

    /**
     * Handles missing required parameters in requests.
     * Triggered when 'city' parameter is not provided.
     *
     * @param ex The exception containing details about the missing parameter
     * @return ResponseEntity with BAD_REQUEST status and error message
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("City name is required");
    }

    /**
     * Handles Weather API specific exceptions.
     * Triggered for invalid cities, API errors, or parsing failures.
     *
     * @param ex The exception containing weather API error details
     * @return ResponseEntity with BAD_REQUEST status and specific error message
     */
    @ExceptionHandler(WeatherApiException.class)
    public ResponseEntity<String> handleWeatherApiException(WeatherApiException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
} 