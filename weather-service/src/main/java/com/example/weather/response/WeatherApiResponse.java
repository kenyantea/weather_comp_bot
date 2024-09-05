package com.example.weather.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiResponse {
    @JsonProperty("days")
    public ArrayList<Day> days;

    public static class Day {
        @JsonProperty("datetime")
        String date;
        @JsonProperty("tempmax")
        public double temp;
        @JsonProperty("feelslikemax")
        public double feelsLike;
        @JsonProperty("humidity")
        public double humidity;
        @JsonProperty("windspeed")
        public double windSpeed;
    }
}

