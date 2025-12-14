package com.healthtourism.weatherservice.controller;
import com.healthtourism.weatherservice.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherController {
    @Autowired
    private WeatherService service;

    @GetMapping("/{city}")
    public ResponseEntity<Map<String, Object>> getWeather(@PathVariable String city) {
        return ResponseEntity.ok(service.getWeather(city));
    }
}

