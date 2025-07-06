package ru.intf.sasha.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {
    
    private String city;
    private double temperature;
    private String description;
    private String main; // Rain, Snow, Clear, Clouds, etc.
    private int humidity;
    private double windSpeed;
    
    // Nested classes for OpenWeatherMap API response
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        @JsonProperty("temp")
        public double temperature;
        
        @JsonProperty("humidity")
        public int humidity;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        @JsonProperty("main")
        public String main;
        
        @JsonProperty("description")
        public String description;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        @JsonProperty("speed")
        public double speed;
    }
    
    // Constructors
    public WeatherData() {}
    
    public WeatherData(String city, double temperature, String description, String main, int humidity, double windSpeed) {
        this.city = city;
        this.temperature = temperature;
        this.description = description;
        this.main = main;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }
    
    // Getters and Setters
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getMain() {
        return main;
    }
    
    public void setMain(String main) {
        this.main = main;
    }
    
    public int getHumidity() {
        return humidity;
    }
    
    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
    
    public double getWindSpeed() {
        return windSpeed;
    }
    
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }
}