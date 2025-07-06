package ru.intf.sasha.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherMapResponse {
    
    @JsonProperty("name")
    private String cityName;
    
    @JsonProperty("main")
    private WeatherData.Main main;
    
    @JsonProperty("weather")
    private List<WeatherData.Weather> weather;
    
    @JsonProperty("wind")
    private WeatherData.Wind wind;
    
    // Constructors
    public OpenWeatherMapResponse() {}
    
    // Getters and Setters
    public String getCityName() {
        return cityName;
    }
    
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    
    public WeatherData.Main getMain() {
        return main;
    }
    
    public void setMain(WeatherData.Main main) {
        this.main = main;
    }
    
    public List<WeatherData.Weather> getWeather() {
        return weather;
    }
    
    public void setWeather(List<WeatherData.Weather> weather) {
        this.weather = weather;
    }
    
    public WeatherData.Wind getWind() {
        return wind;
    }
    
    public void setWind(WeatherData.Wind wind) {
        this.wind = wind;
    }
}