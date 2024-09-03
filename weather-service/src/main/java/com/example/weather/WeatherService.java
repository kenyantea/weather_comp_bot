package com.example.weather;

import com.example.weather.response.WeatherApiResponse;
import com.example.weather.response.WeatherServiceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

@Service
public class WeatherService {

    private final String API_KEY = "WEJEXQUD9SQCLE2QWA55ALEWJ"; // ключ, доступный после регистрации
    private static final String format = "yyyy-MM-dd";
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);


    // эта функция должна возвращать результат работы всего сервиса в виде json
    // 1) среднее значение по заданному параметру
    // 2) минимум
    // 3) максимум
    // 4) всего принятых параметров
    // 5) путь к картинке с графиком (позже)
    public String processWeather(String city, String parameter, String startDate, String endDate) {
        String r = getWeatherByApi(city, startDate, endDate);

        if (r.contains("\"error\":")) {
            return r;
        }

        ArrayList<Double> params = processResponse(r, parameter);
        if (params.isEmpty()) {
            return "{\"error\": \"Нет данных для заданного города и параметра.\"}"; // Возвращаем сообщение об ошибке
        }

        double average = averageWeather(params),
                max = Collections.max(params),
                min = Collections.min(params);
        int total = params.size();

        WeatherServiceResponse response = new WeatherServiceResponse();
        response.average = average;
        response.min = min;
        response.max = max;
        response.totalParameters = total;

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";

        try {
            jsonString = objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            e.printStackTrace();
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
            return "{\"error\": \"Ошибка во время обращения к API: " + e.getMessage() + "\"}";
        }
    }

    private String handleHttpError(HttpResponse<String> response) {
        String errorMessage = switch (response.statusCode()) {
            case 400 -> "Неверный запрос. Проверьте введенные данные.";
            case 404 -> "Город не найден. Проверьте, правильное ли имя города.";
            case 500 -> "Ошибка сервера. Попробуйте позже.";
            default -> "Произошла ошибка: " + response.statusCode();
        };

        return "{\"error\": \"" + errorMessage + "\"}";
    }

    public ArrayList<Double> processResponse(String resp, String parameter) {
        ObjectMapper objectMapper = new ObjectMapper();
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

