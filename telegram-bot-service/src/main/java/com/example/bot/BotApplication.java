package com.example.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class BotApplication {
	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(BotApplication.class, args);
	}
}