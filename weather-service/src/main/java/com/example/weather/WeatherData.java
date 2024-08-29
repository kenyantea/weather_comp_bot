package com.example.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {
    @Getter
    private Main main;
    private String name;
    @Getter
    private Weather[] weather;

    @Setter
    @Getter
    public static class Main {
        // Геттеры и сеттеры
        private float temp;
        private int pressure;
        private int humidity;

    }
    @Setter
    @Getter
    public static class Weather {
        // Геттеры и сеттеры
        private String description;

    }
}

