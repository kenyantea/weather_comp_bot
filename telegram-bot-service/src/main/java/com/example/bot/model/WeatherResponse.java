package com.example.bot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {
    @JsonProperty("avg")
    public double average;

    @JsonProperty("min")
    public double min;

    @JsonProperty("max")
    public double max;

    @JsonProperty("total")
    public int totalParameters;

    @JsonProperty("parameters")
    public List<Double> parameters;

    public String error;
}
