package com.example.bot;

import com.example.bot.service.BotService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Getter
@Configuration
public class BotConfig {

    @Autowired
    public BotConfig(){
        botUsername = "weather_comp_bot";
        botToken = "7503976318:AAF2fZ5mThVyamp8X_uMtT7lwPxvA0R7xoY";
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(BotService entryBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(entryBot);
        return api;
    }

    private final String botUsername;
    private final String botToken;
}
