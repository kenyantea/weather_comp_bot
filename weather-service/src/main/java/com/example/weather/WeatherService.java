package com.example.weather;

import com.example.weather.WeatherData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private final String API_KEY = "e8b5caa738a03c240fd7c821faa755ed"; // замените на свой ключ

    public WeatherData getWeather(String city) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.openweathermap.org/data/2.5/weather?appid=" + API_KEY +
                "&q=" + city + "&units=metric&lang=ru&exclude=minutely,hourly,alerts";
        return restTemplate.getForObject(url, WeatherData.class);
    }
}

