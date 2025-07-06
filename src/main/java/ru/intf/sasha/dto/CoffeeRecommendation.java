package ru.intf.sasha.dto;

import ru.intf.sasha.model.Coffee;

public class CoffeeRecommendation {
    
    private Coffee recommendedCoffee;
    private WeatherData weatherData;
    private String recommendationReason;
    private double confidenceScore; // 0.0 to 1.0
    
    // Constructors
    public CoffeeRecommendation() {}
    
    public CoffeeRecommendation(Coffee recommendedCoffee, WeatherData weatherData, String recommendationReason, double confidenceScore) {
        this.recommendedCoffee = recommendedCoffee;
        this.weatherData = weatherData;
        this.recommendationReason = recommendationReason;
        this.confidenceScore = confidenceScore;
    }
    
    // Getters and Setters
    public Coffee getRecommendedCoffee() {
        return recommendedCoffee;
    }
    
    public void setRecommendedCoffee(Coffee recommendedCoffee) {
        this.recommendedCoffee = recommendedCoffee;
    }
    
    public WeatherData getWeatherData() {
        return weatherData;
    }
    
    public void setWeatherData(WeatherData weatherData) {
        this.weatherData = weatherData;
    }
    
    public String getRecommendationReason() {
        return recommendationReason;
    }
    
    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }
    
    public double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
}