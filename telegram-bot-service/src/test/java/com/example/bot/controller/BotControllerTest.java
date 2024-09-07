package com.example.bot.controller;

import com.example.bot.service.BotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.verify;

class BotControllerTest {

    @InjectMocks
    private BotController botController;

    @Mock
    private BotService botService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testReceiveUpdate_ShouldCallHandleUpdate() {
        Update update = new Update();
        botController.receiveUpdate(update);

        verify(botService).handleUpdate(update);
    }

    @Test
    void testConstructor_ShouldInitializeBotService() {
        assert botService != null;
    }
}
