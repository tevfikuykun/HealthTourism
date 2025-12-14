package com.healthtourism.weatherservice.service;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class WeatherService {
    public Map<String, Object> getWeather(String city) {
        Map<String, Object> weather = new HashMap<>();
        weather.put("city", city);
        weather.put("temperature", 22);
        weather.put("condition", "Sunny");
        weather.put("humidity", 65);
        return weather;
    }
}

