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

    @GetMapping("/v1/process_daily")
    public String getWeatherDaily(@RequestParam("city") String city,
                             @RequestParam("param") String parameter,
                             @RequestParam("start") String startDate,
                             @RequestParam("end") String endDate) {
        return weatherService.processWeatherDaily(city, parameter, startDate, endDate);
    }

    @GetMapping("/v1/process_yearly")
    public String getWeatherYearly(@RequestParam("day") String day,
                                   @RequestParam("city") String city,
                                   @RequestParam("param") String parameter,
                                   @RequestParam("start") String startDate,
                                   @RequestParam("end") String endDate) {
        return weatherService.processWeatherYearly(city, parameter, startDate, endDate, day);
    }
}
