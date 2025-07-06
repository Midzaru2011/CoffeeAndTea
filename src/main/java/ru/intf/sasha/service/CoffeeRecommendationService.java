package ru.intf.sasha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.intf.sasha.dto.CoffeeRecommendation;
import ru.intf.sasha.dto.WeatherData;
import ru.intf.sasha.model.Coffee;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class CoffeeRecommendationService {
    
    @Autowired
    private CoffeeService coffeeService;
    
    @Autowired
    private WeatherService weatherService;
    
    private final Random random = new Random();
    
    public CoffeeRecommendation getRecommendationByCity(String city) {
        WeatherData weather = weatherService.getWeatherByCity(city);
        return getRecommendationByWeather(weather);
    }
    
    public CoffeeRecommendation getRecommendationByWeather(WeatherData weather) {
        List<Coffee> allCoffees = coffeeService.getAllCoffees();
        
        if (allCoffees.isEmpty()) {
            // Если в базе нет кофе, создадим базовые варианты
            allCoffees = createDefaultCoffees();
        }
        
        Coffee recommendedCoffee = selectCoffeeBasedOnWeather(weather, allCoffees);
        String reason = generateRecommendationReason(weather, recommendedCoffee);
        double confidence = calculateConfidenceScore(weather);
        
        return new CoffeeRecommendation(recommendedCoffee, weather, reason, confidence);
    }
    
    private Coffee selectCoffeeBasedOnWeather(WeatherData weather, List<Coffee> availableCoffees) {
        String weatherCondition = weather.getMain();
        double temperature = weather.getTemperature();
        
        // Логика выбора кофе на основе погоды
        if (temperature < 0) {
            // Очень холодно - горячий крепкий кофе
            return findCoffeeByKeyword(availableCoffees, new String[]{"эспрессо", "американо", "горячий", "крепкий"});
        } else if (temperature < 10) {
            // Холодно - теплый кофе с молоком
            return findCoffeeByKeyword(availableCoffees, new String[]{"латте", "капучино", "мокко"});
        } else if (temperature > 25) {
            // Жарко - холодный кофе
            return findCoffeeByKeyword(availableCoffees, new String[]{"айс", "холодный", "фраппе", "ice"});
        } else if ("Rain".equalsIgnoreCase(weatherCondition) || "Drizzle".equalsIgnoreCase(weatherCondition)) {
            // Дождь - уютный кофе
            return findCoffeeByKeyword(availableCoffees, new String[]{"латте", "капучино", "мокко", "горячий"});
        } else if ("Snow".equalsIgnoreCase(weatherCondition)) {
            // Снег - согревающий кофе
            return findCoffeeByKeyword(availableCoffees, new String[]{"горячий", "американо", "мокко", "эспрессо"});
        } else if ("Clear".equalsIgnoreCase(weatherCondition)) {
            // Ясно - легкий кофе
            return findCoffeeByKeyword(availableCoffees, new String[]{"американо", "капучино", "латте"});
        }
        
        // По умолчанию возвращаем случайный кофе
        return availableCoffees.get(random.nextInt(availableCoffees.size()));
    }
    
    private Coffee findCoffeeByKeyword(List<Coffee> coffees, String[] keywords) {
        for (String keyword : keywords) {
            for (Coffee coffee : coffees) {
                if (coffee.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                    coffee.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    return coffee;
                }
            }
        }
        
        // Если не найден по ключевым словам, возвращаем случайный
        return coffees.get(random.nextInt(coffees.size()));
    }
    
    private String generateRecommendationReason(WeatherData weather, Coffee coffee) {
        double temp = weather.getTemperature();
        String condition = weather.getMain();
        String description = weather.getDescription();
        
        StringBuilder reason = new StringBuilder();
        reason.append("На улице ").append(description);
        reason.append(", температура ").append(Math.round(temp)).append("°C. ");
        
        if (temp < 0) {
            reason.append("В такую холодную погоду рекомендуем согревающий ");
        } else if (temp < 10) {
            reason.append("При прохладной погоде отлично подойдет ");
        } else if (temp > 25) {
            reason.append("В жаркую погоду освежит ");
        } else if ("Rain".equalsIgnoreCase(condition)) {
            reason.append("Дождливый день требует уютного ");
        } else if ("Snow".equalsIgnoreCase(condition)) {
            reason.append("Снежная погода - время для горячего ");
        } else {
            reason.append("При такой погоде прекрасно подойдет ");
        }
        
        reason.append(coffee.getName()).append(". ");
        reason.append(coffee.getDescription());
        
        return reason.toString();
    }
    
    private double calculateConfidenceScore(WeatherData weather) {
        // Простая логика для расчета уверенности в рекомендации
        double base = 0.7;
        
        // Увеличиваем уверенность для экстремальных условий
        if (Math.abs(weather.getTemperature()) > 20 || weather.getTemperature() < 0) {
            base += 0.2;
        }
        
        if ("Rain".equalsIgnoreCase(weather.getMain()) || 
            "Snow".equalsIgnoreCase(weather.getMain())) {
            base += 0.1;
        }
        
        return Math.min(base, 1.0);
    }
    
    private List<Coffee> createDefaultCoffees() {
        List<Coffee> defaultCoffees = new ArrayList<>();
        
        defaultCoffees.add(new Coffee("Эспрессо", "Крепкий черный кофе", 150.0, "espresso.jpg"));
        defaultCoffees.add(new Coffee("Американо", "Эспрессо с горячей водой", 180.0, "americano.jpg"));
        defaultCoffees.add(new Coffee("Латте", "Кофе с молоком и молочной пеной", 220.0, "latte.jpg"));
        defaultCoffees.add(new Coffee("Капучино", "Эспрессо с вспененным молоком", 200.0, "cappuccino.jpg"));
        defaultCoffees.add(new Coffee("Мокко", "Кофе с шоколадом и молоком", 250.0, "mocha.jpg"));
        defaultCoffees.add(new Coffee("Айс Латте", "Холодный латте со льдом", 240.0, "ice_latte.jpg"));
        defaultCoffees.add(new Coffee("Фраппе", "Холодный кофейный коктейль", 280.0, "frappe.jpg"));
        
        return defaultCoffees;
    }
}