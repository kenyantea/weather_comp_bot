package com.example.bot;

import com.example.bot.service.BotService;
import com.example.bot.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class BotApplication {
	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(BotApplication.class, args);
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

		// Register our bot
		try {
			botsApi.registerBot(new BotService(new UserService(),new RestTemplate(), new BotConfig("weather_comp_bot", "7503976318:AAF2fZ5mThVyamp8X_uMtT7lwPxvA0R7xoY")));
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}