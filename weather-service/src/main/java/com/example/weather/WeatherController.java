package com.example.weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/v1/processdata")
    public String getWeather(@RequestParam("city") String city,
                             @RequestParam("param") String parameter,
                             @RequestParam("start") String startDate,
                             @RequestParam("end") String endDate) {
        return weatherService.processWeather(city, parameter, startDate, endDate);
    }
}
