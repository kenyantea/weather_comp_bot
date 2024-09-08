package com.example.bot.service;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Epic("Config tests")
class BotConfigTest {

    @InjectMocks
    private BotConfig botConfig;

    @InjectMocks
    private BotService botService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Description("bot initializes")
    void testConstructor_ShouldInitializeBotUsernameAndToken() {
        assertEquals("weather_comp_bot", botConfig.getBotUsername());
        assertEquals("7503976318:AAF2fZ5mThVyamp8X_uMtT7lwPxvA0R7xoY", botConfig.getBotToken());
    }

    @Test
    @Description("RestTemplate appears")
    void testRestTemplate_ShouldReturnNewRestTemplate() {
        RestTemplate restTemplate = botConfig.restTemplate();
        assertNotNull(restTemplate);
    }
}
