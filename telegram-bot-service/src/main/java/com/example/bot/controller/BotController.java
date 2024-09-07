package com.example.bot.controller;

import com.example.bot.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("/bot")
public class BotController {

    private final BotService botService;
    @Autowired
    public BotController(BotService telegramBotService) {
        this.botService = telegramBotService;
    }

    @PostMapping("/webhook")
    public void receiveUpdate(@RequestBody Update update) {
        botService.handleUpdate(update);
    }
}
