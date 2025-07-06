package ru.intf.sasha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.intf.sasha.dto.CoffeeRecommendation;
import ru.intf.sasha.dto.WeatherData;
import ru.intf.sasha.service.CoffeeRecommendationService;
import ru.intf.sasha.service.WeatherService;

@Controller
@RequestMapping("/recommendations")
public class WeatherRecommendationController {
    
    @Autowired
    private CoffeeRecommendationService coffeeRecommendationService;
    
    @Autowired
    private WeatherService weatherService;
    
    /**
     * Главная страница для рекомендаций кофе
     */
    @GetMapping
    public String recommendationPage() {
        return "recommendations";
    }
    
    /**
     * API endpoint для получения рекомендации по городу
     */
    @GetMapping("/api/by-city")
    @ResponseBody
    public ResponseEntity<CoffeeRecommendation> getRecommendationByCity(@RequestParam String city) {
        try {
            CoffeeRecommendation recommendation = coffeeRecommendationService.getRecommendationByCity(city);
            return ResponseEntity.ok(recommendation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * API endpoint для получения данных о погоде
     */
    @GetMapping("/api/weather")
    @ResponseBody
    public ResponseEntity<WeatherData> getWeather(@RequestParam String city) {
        try {
            WeatherData weather = weatherService.getWeatherByCity(city);
            return ResponseEntity.ok(weather);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Страница с рекомендацией для конкретного города
     */
    @GetMapping("/city/{cityName}")
    public String getRecommendationPageForCity(@PathVariable String cityName, Model model) {
        try {
            CoffeeRecommendation recommendation = coffeeRecommendationService.getRecommendationByCity(cityName);
            model.addAttribute("recommendation", recommendation);
            model.addAttribute("city", cityName);
            return "recommendation-result";
        } catch (Exception e) {
            model.addAttribute("error", "Не удалось получить рекомендацию для города: " + cityName);
            return "recommendation-result";
        }
    }
    
    /**
     * POST endpoint для получения рекомендации с детальными погодными данными
     */
    @PostMapping("/api/by-weather")
    @ResponseBody
    public ResponseEntity<CoffeeRecommendation> getRecommendationByWeather(@RequestBody WeatherData weatherData) {
        try {
            CoffeeRecommendation recommendation = coffeeRecommendationService.getRecommendationByWeather(weatherData);
            return ResponseEntity.ok(recommendation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}