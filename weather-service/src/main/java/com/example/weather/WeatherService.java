package com.example.weather;

import com.example.weather.response.WeatherApiResponse;
import com.example.weather.response.WeatherServiceResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static org.jfree.chart.ChartUtils.saveChartAsPNG;

@Service
public class WeatherService {

    private final String API_KEY = "WEJEXQUD9SQCLE2QWA55ALEWJ"; // the API key that is available after the registration
    private static final String format = "yyyy-MM-dd";

    // this function returns json
    public String processWeatherDaily(String city, String parameter, String startDate, String endDate) {
        String r = getWeatherByApi(city, startDate, endDate);

        if (r.contains("\"error\":")) {
            return r;
        }

        ArrayList<Double> params = processResponse(r, parameter);

        return analyzeWeather(params, parameter, startDate, endDate);
    }

    public String processWeatherYearly(String city, String parameter, String startYear, String endYear, String day) {
        ArrayList<Double> params = new ArrayList<>();

        String[] dayParts = day.split("-");
        String month = dayParts[0];
        String date = dayParts[1];

        int startYearInt = Integer.parseInt(startYear);
        int endYearInt = Integer.parseInt(endYear);

        for (int year = startYearInt; year <= endYearInt; year++) {
            String currentDate = year + "-" + month + "-" + date;
            String r = getWeatherByApi(city, currentDate, currentDate);
            if (r.contains("\"error\":")) {
                return r; 
            }
            List<Double> currentParams = processResponse(r, parameter);
            params.addAll(currentParams);
        }

        return analyzeWeather(params, parameter, startYear, endYear);
    }

    private String analyzeWeather(ArrayList<Double> params, String parameter, String startYear, String endYear) {
        if (params.isEmpty()) {
            return "{\"error\": \"Sorry, I couldn't get data for this city and parameter.\"}";
        }

        double average = averageWeather(params);
        double max = Collections.max(params);
        double min = Collections.min(params);
        int total = params.size();

        WeatherServiceResponse response = new WeatherServiceResponse();
        DecimalFormat df = new DecimalFormat("#.##");
        response.average = Double.parseDouble(df.format(average).replace(",", "."));
        response.min = Double.parseDouble(df.format(min).replace(",", "."));
        response.max = Double.parseDouble(df.format(max).replace(",", "."));
        response.totalParameters = total;
        response.parameters = params;

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString;

        try {
            jsonString = objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
            jsonString = "{\"error\": \"There was an unexpected error. Try again later :)\"}";
        }

        return jsonString;
    }

    public String getWeatherByApi(String city, String startDate, String endDate) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"
                        + city + "/" + startDate + "/" + endDate
                        + "?unitGroup=metric&elements=datetime%2Ctempmax%2Cfeelslikemax%2Chumidity%2Cwindspeed"
                        + "&include=days&key="
                        + API_KEY + "&contentType=json"))
                .method("GET", HttpRequest.BodyPublishers.noBody()).build();

        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response.body();
            } else {
                return handleHttpError(response);
            }
        } catch (IOException | InterruptedException e) {
            return "{\"error\": \"API access weather: " + e.getMessage() + "\"}";
        }
    }

    String handleHttpError(HttpResponse<String> response) {
        String errorMessage = switch (response.statusCode()) {
            case 400 -> "Invalid request. Check the entered data.";
            case 404 -> "The city was not found. Check if the name of the city is correct.";
            case 500 -> "Server error. Try again later.";
            default -> "There was an error: " + response.statusCode();
        };

        return "{\"error\": \"" + errorMessage + "\"}";
    }

    public ArrayList<Double> processResponse(String resp, String parameter) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ArrayList<Double> temperatures = new ArrayList<>();
        try {
            WeatherApiResponse response = objectMapper.readValue(resp, WeatherApiResponse.class);
            for (WeatherApiResponse.Day day : response.days) {
                switch(parameter) {
                    case "temp": // имена взяты из json
                        temperatures.add(day.temp);
                        break;
                    case "humidity":
                        temperatures.add(day.humidity);
                        break;
                    case "feelslike":
                        temperatures.add(day.feelsLike);
                        break;
                    case "windspeed":
                        temperatures.add(day.windSpeed);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temperatures;
    }

    public double averageWeather(ArrayList<Double> params) {
        double sum = 0;
        int i = 0;
        for (Double param : params) {
            sum += param;
            i++;
        }
        return sum / i;
    }
}

