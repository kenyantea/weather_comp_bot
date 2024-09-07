package com.example.weather;

import com.example.weather.response.WeatherApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceTests {

    private WeatherService weatherService;
    @Mock
    private WeatherService weatherServiceMock;
    private WeatherApiResponse mockApiResponse;

    @BeforeEach
    void setUp() {
        weatherService = new WeatherService();
        mockApiResponse = mock(WeatherApiResponse.class);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessWeatherDaily_WhenSuccessful_ShouldReturnValidJson() {
        String result = weatherService.processWeatherDaily("Moscow", "temp", "2024-09-01", "2024-09-05");
        assertNotNull(result);
        assertTrue(result.contains("\"avg\":"));
        assertTrue(result.contains("\"max\":"));
        assertTrue(result.contains("\"min\":"));
    }

    @Test
    void testProcessWeatherDaily_WhenApiReturnsError_ShouldReturnErrorJson() {
        assertTrue(weatherService.getWeatherByApi("UnknownCity", "temp", "2024-09-05").contains("\"error\""));
    }

    @Test
    void testAverageWeather_WithValidParameters_ShouldReturnCorrectAverage() {
        ArrayList<Double> params = new ArrayList<>(Collections.singletonList(10.0));
        double average = weatherService.averageWeather(params);
        assertEquals(10.0, average);
    }

    @Test
    void testAverageWeather_WithEmptyList_ShouldReturnNan() {
        ArrayList<Double> params = new ArrayList<>();
        double average = weatherService.averageWeather(params);
        assertTrue(Double.isNaN(average));
    }

    @Test
    void testHandleHttpError_ShouldReturnProperErrorMessage() {
        int statusCode = 404;
        var response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(statusCode);
        String error = weatherService.handleHttpError(response);
        assertTrue(error.contains("The city was not found."));
    }

    @Test
    void testProcessWeatherYearly_WhenDataIsAvailable_ShouldReturnCorrectWeatherStats() {
        String city = "Moscow";
        String parameter = "temp";
        String startYear = "2020";
        String endYear = "2022";
        String day = "09-01";

        when(weatherServiceMock.getWeatherByApi(city, "2020-09-01", "2020-09-01"))
                .thenReturn(getMockWeatherResponse(20.0, 30.0, 15.0));
        when(weatherServiceMock.getWeatherByApi(city, "2021-09-01", "2021-09-01"))
                .thenReturn(getMockWeatherResponse(22.0, 32.0, 18.0));
        when(weatherServiceMock.getWeatherByApi(city, "2022-09-01", "2022-09-01"))
                .thenReturn(getMockWeatherResponse(25.0, 35.0, 20.0));

        when(weatherServiceMock.processResponse(anyString(), eq(parameter)))
                .thenReturn(new ArrayList<>(Arrays.asList(20.0, 22.0, 25.0)));

        String result = weatherService.processWeatherYearly(city, parameter, startYear, endYear, day);

        assertNotNull(result);
        assertTrue(result.contains("\"avg\":"));
        assertTrue(result.contains("\"max\":"));
        assertTrue(result.contains("\"min\":"));
    }

    @Test
    void testProcessWeatherYearly_WhenApiReturnsError_ShouldReturnErrorJson() {
        // Настройка
        String city = "UnknownCity";
        String parameter = "temp";
        String startYear = "2020";
        String endYear = "2022";
        String day = "09-01";

        when(weatherServiceMock.getWeatherByApi(city, "2020-09-01", "2020-09-01"))
                .thenReturn("{\"error\": \"City not found\"}");

        String result = weatherService.processWeatherYearly(city, parameter, startYear, endYear, day);

        assertNotNull(result);
        assertTrue(result.contains("\"error\":"));
    }

    private String getMockWeatherResponse(double temp, double maxTemp, double minTemp) {
        return String.format("{\"days\": [{\"temp\": %.1f, \"tempmax\": %.1f, \"tempmin\": %.1f}]}", temp, maxTemp, minTemp);
    }
}
