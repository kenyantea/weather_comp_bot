package com.example.weather;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @Tag(name="getWeatherDaily", description="Every day in the specified period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public String getWeatherDaily(@RequestParam("city") @Parameter(description = "City name") String city,
                             @RequestParam("param") @Parameter(description = "Parameter: temperature, feels like temperature, humidity or wind speed") String parameter,
                             @RequestParam("start") @Parameter(description = "Start date in format YYYY-MM-DD") String startDate,
                             @RequestParam("end") @Parameter(description = "End date in format YYYY-MM-DD") String endDate) {
        return weatherService.processWeatherDaily(city, parameter, startDate, endDate);
    }

    @GetMapping("/v1/process_yearly")
    @Tag(name="getWeatherYearly", description="Particular day in the specified period")
    public String getWeatherYearly(@RequestParam("day") @Parameter(description = "City name") String day,
                                   @RequestParam("city") @Parameter(description = "Particular day in format MM-DD") String city,
                                   @RequestParam("param") @Parameter(description = "Parameter: temperature, feels like temperature, humidity or wind speed") String parameter,
                                   @RequestParam("start") @Parameter(description = "Start date in format YYYY-MM-DD") String startDate,
                                   @RequestParam("end") @Parameter(description = "End date in format YYYY-MM-DD") String endDate) {
        return weatherService.processWeatherYearly(city, parameter, startDate, endDate, day);
    }
}
