package com.example.bot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void testConstructor_ShouldInitializeBotUsernameAndToken() {
        // Проверка, что бот инициализирован
        assertEquals("weather_comp_bot", botConfig.getBotUsername());
        assertEquals("7503976318:AAF2fZ5mThVyamp8X_uMtT7lwPxvA0R7xoY", botConfig.getBotToken());
    }

    @Test
    void testRestTemplate_ShouldReturnNewRestTemplate() {
        // Проверка создания RestTemplate
        RestTemplate restTemplate = botConfig.restTemplate();
        assertNotNull(restTemplate);
    }
}
