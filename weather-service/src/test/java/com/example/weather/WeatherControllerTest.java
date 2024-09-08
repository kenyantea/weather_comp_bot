package com.example.weather;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Epic("Controller tests")
class WeatherControllerTest {

    @InjectMocks
    private WeatherController weatherController;

    @Mock
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Description("process_daily endpoint works well")
    void testGetWeatherDaily_WhenValidParams_ShouldReturnWeatherData() {
        String city = "Moscow";
        String parameter = "temp";
        String startDate = "2024-09-01";
        String endDate = "2024-09-05";
        String expectedResponse = "{\"average\": 25.5, \"max\": 30.0, \"min\": 20.0}";

        when(weatherService.processWeatherDaily(city, parameter, startDate, endDate)).thenReturn(expectedResponse);

        String jsonResponse = weatherController.getWeatherDaily(city, parameter, startDate, endDate);

        assertEquals(expectedResponse, jsonResponse);
        verify(weatherService).processWeatherDaily(city, parameter, startDate, endDate);
    }

    @Test
    @Description("process_daily endpoint processes error")
    void testGetWeatherDaily_WhenServiceReturnsError_ShouldReturnErrorJson() {
        String city = "UnknownCity";
        String parameter = "temp";
        String startDate = "2024-09-01";
        String endDate = "2024-09-05";
        String expectedErrorResponse = "{\"error\": \"City not found\"}";

        when(weatherService.processWeatherDaily(city, parameter, startDate, endDate)).thenReturn(expectedErrorResponse);

        String jsonResponse = weatherController.getWeatherDaily(city, parameter, startDate, endDate);

        assertEquals(expectedErrorResponse, jsonResponse);
        verify(weatherService).processWeatherDaily(city, parameter, startDate, endDate);
    }

    @Test
    @Description("process_yearly endpoint works well")
    void testGetWeatherYearly_WhenValidParams_ShouldReturnYearlyWeatherData() {
        String day = "09-01";
        String city = "Moscow";
        String parameter = "temp";
        String startDate = "2020";
        String endDate = "2024";
        String expectedResponse = "{\"average\": 20.5, \"max\": 28.0, \"min\": 15.0}";

        when(weatherService.processWeatherYearly(city, parameter, startDate, endDate, day)).thenReturn(expectedResponse);

        String jsonResponse = weatherController.getWeatherYearly(day, city, parameter, startDate, endDate);

        assertEquals(expectedResponse, jsonResponse);
        verify(weatherService).processWeatherYearly(city, parameter, startDate, endDate, day);
    }

    @Test
    @Description("process_yearly endpoint processes error")
    void testGetWeatherYearly_WhenServiceReturnsError_ShouldReturnErrorJson() {
        String day = "09-01";
        String city = "UnknownCity";
        String parameter = "temp";
        String startDate = "2020";
        String endDate = "2024";
        String expectedErrorResponse = "{\"error\": \"City not found\"}";

        when(weatherService.processWeatherYearly(city, parameter, startDate, endDate, day)).thenReturn(expectedErrorResponse);

        String jsonResponse = weatherController.getWeatherYearly(day, city, parameter, startDate, endDate);

        assertEquals(expectedErrorResponse, jsonResponse);
        verify(weatherService).processWeatherYearly(city, parameter, startDate, endDate, day);
    }
}
