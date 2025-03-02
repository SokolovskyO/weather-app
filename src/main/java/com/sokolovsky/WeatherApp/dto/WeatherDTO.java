package com.sokolovsky.WeatherApp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "weather",
    "temperature",
    "visibility",
    "wind",
    "datetime",
    "sys",
    "timezone",
    "name"
})
public class WeatherDTO {
    @JsonProperty("weather")
    private List<Weather> weatherList;

    @JsonProperty(value = "main", access = JsonProperty.Access.WRITE_ONLY)
    private Main main;

    private Integer visibility;
    private Wind wind;
    
    @Getter(AccessLevel.NONE)
    private Long dt;
    
    private Sys sys;
    private Integer timezone;
    private String name;

    @JsonProperty("weather")
    public Weather getWeather() {
        return weatherList != null && !weatherList.isEmpty() ? weatherList.get(0) : null;
    }

    @JsonProperty("temperature")
    public Main getTemperature() {
        return main;
    }

    @JsonProperty("datetime")
    public Long getDatetime() {
        return dt;
    }

    @JsonProperty("dt")
    public void setDt(Long value) {
        this.dt = value;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String main;
        private String description;
    }
    
    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private Double temp;
        
        @JsonProperty("feels_like")
        private Double feelsLike;
    }
    
    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        private Double speed;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        private Long sunrise;
        private Long sunset;
    }
}