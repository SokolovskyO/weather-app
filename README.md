# Weather App SDK

## Introduction
Weather App SDK is a Spring Boot based SDK for retrieving weather information using the OpenWeather API. The SDK provides caching capabilities, automatic data polling, and a clean interface for weather data retrieval in Java applications.

## Features
- Real-time weather data retrieval
- Built-in caching (10 cities, 10 minutes TTL)
- Two operation modes: on-demand and polling
- Geocoding support
- Comprehensive error handling
- Thread-safe implementation

## Installation

### Prerequisites
- Java 17+
- Maven 3.6+
- OpenWeather API key ([Get it here](https://openweathermap.org/api))

### Quick Start

1. Build the project:

bash
./mvnw clean install

2. Run the application:

bash
./mvnw spring-boot:run "-Dspring-boot.run.arguments=--api.key=YOUR_API_KEY"

## Configuration

### Application Properties
Create or modify `application.yml`:

yaml
weather:
mode: ON_DEMAND # or POLLING for automatic updates
geo-url: http://api.openweathermap.org/geo/1.0/direct
weather-url: https://api.openweathermap.org/data/2.5/weather

### Operation Modes
- **ON_DEMAND**: Updates weather data only when requested
- **POLLING**: Automatically updates cached data every 10 minutes

### Swagger UI
After starting the application, Swagger UI is available at:

http://localhost:8080/swagger-ui/index.html

#### Get Weather

Response:

{
"weather": {
"main": "Clouds",
"description": "overcast clouds"
},
"temperature": {
"temp": -3.53,
"feels_like": -3.53
},
"visibility": 683,
"wind": {
"speed": 0.95
},
"datetime": 1740934487,
"sys": {
"sunrise": 1740888499,
"sunset": 1740926935
},
"timezone": 10800,
"name": "Kostroma"
}

## Testing
bash
./mvnw test