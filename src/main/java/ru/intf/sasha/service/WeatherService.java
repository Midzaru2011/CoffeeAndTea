package ru.intf.sasha.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.intf.sasha.dto.OpenWeatherMapResponse;
import ru.intf.sasha.dto.WeatherData;
import reactor.core.publisher.Mono;

@Service
public class WeatherService {
    
    private final WebClient webClient;
    
    @Value("${weather.api.key:demo_key}")
    private String apiKey;
    
    @Value("${weather.api.base-url:https://api.openweathermap.org/data/2.5}")
    private String baseUrl;
    
    public WeatherService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    public WeatherData getWeatherByCity(String city) {
        try {
            // Для демонстрации, если нет реального API ключа, вернем моковые данные
            if ("demo_key".equals(apiKey)) {
                return createMockWeatherData(city);
            }
            
            String url = String.format("%s/weather?q=%s&appid=%s&units=metric&lang=ru", 
                                     baseUrl, city, apiKey);
            
            Mono<OpenWeatherMapResponse> responseMono = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(OpenWeatherMapResponse.class);
            
            OpenWeatherMapResponse response = responseMono.block();
            return convertToWeatherData(response);
            
        } catch (Exception e) {
            // Если произошла ошибка, вернем моковые данные
            return createMockWeatherData(city);
        }
    }
    
    private WeatherData convertToWeatherData(OpenWeatherMapResponse response) {
        WeatherData weatherData = new WeatherData();
        weatherData.setCity(response.getCityName());
        
        if (response.getMain() != null) {
            weatherData.setTemperature(response.getMain().temperature);
            weatherData.setHumidity(response.getMain().humidity);
        }
        
        if (response.getWeather() != null && !response.getWeather().isEmpty()) {
            WeatherData.Weather weather = response.getWeather().get(0);
            weatherData.setMain(weather.main);
            weatherData.setDescription(weather.description);
        }
        
        if (response.getWind() != null) {
            weatherData.setWindSpeed(response.getWind().speed);
        }
        
        return weatherData;
    }
    
    private WeatherData createMockWeatherData(String city) {
        // Создаем разные моковые данные в зависимости от города для демонстрации
        switch (city.toLowerCase()) {
            case "moscow":
            case "москва":
                return new WeatherData(city, -5.0, "снег", "Snow", 85, 3.5);
            case "sochi":
            case "сочи":
                return new WeatherData(city, 15.0, "дождь", "Rain", 75, 2.1);
            case "novosibirsk":
            case "новосибирск":
                return new WeatherData(city, -15.0, "ясно", "Clear", 45, 1.8);
            case "yekaterinburg":
            case "екатеринбург":
                return new WeatherData(city, 2.0, "облачно", "Clouds", 65, 4.2);
            default:
                return new WeatherData(city, 10.0, "переменная облачность", "Clouds", 60, 2.5);
        }
    }
}