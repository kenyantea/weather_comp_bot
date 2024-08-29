package com.example.bot.service;

import com.example.bot.model.UpdateData;
import com.example.bot.model.User;
import com.example.bot.BotConfig;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
public class BotService extends TelegramLongPollingBot {

    private UserService userService;
    //private final WeatherService weatherService;
    private RestTemplate restTemplate;
    private BotConfig botConfiguration;

    private String currentCity;
    private String currentParameter;
    private String currentDateRange;

//    @Autowired
//    public BotService(UserService userService, WeatherService weatherService,
//                                   RestTemplate restTemplate, BotConfig botConfiguration) {
//        this.userService = userService;
//        this.weatherService = weatherService;
//        this.restTemplate = restTemplate;
//        this.botConfiguration = botConfiguration;
//    }

    @Autowired
    public BotService(UserService userService,
                      RestTemplate restTemplate, BotConfig botConfiguration) {
        this.userService = userService;
        this.restTemplate = restTemplate;
        this.botConfiguration = botConfiguration;
    }

    @Override
    public String getBotUsername() {
        return botConfiguration.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfiguration.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            String userName = message.getFrom().getFirstName();

            User user = userService.getUserByChatId(chatId);
            if (user == null) {
                user = userService.registerNewUser(chatId, userName);
                SendMessage mes = new SendMessage();
                mes.setChatId(chatId);
                mes.setText("Привет! Я бот для анализа погоды. Запомнил твоё имя: " + userName);
                try {
                    execute(mes); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                mes.setText("Введите город, для которого вы хотите получить данные о погоде:");
                try {
                    execute(mes); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else {
                processUserInput(message, user);
            }
        }
    }

    public void handleUpdate(UpdateData update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String userName = message.getFrom().getFirstName();

            User user = userService.getUserByChatId(chatId);
            if (user == null) {
                user = userService.registerNewUser(chatId, userName);
                sendMessage(chatId, "Привет! Я бот для анализа погоды. Запомнил твоё имя: " + userName);
                sendMessage(chatId, "Введите город, для которого вы хотите получить данные о погоде:");
            } else {
                processUserInput(message, user);
            }
        }
    }

    private void processUserInput(Message message, User user) {
        if (currentCity == null) {
            currentCity = message.getText();
            sendMessage(user.getChatId(), "Отлично! Теперь выберите интересующий вас параметр погоды:");
            sendParameterButtons(user.getChatId());
        } else if (currentParameter == null) {
            currentParameter = message.getText();
            sendMessage(user.getChatId(), "Введите диапазон дат в формате YYYY-MM-DD - YYYY-MM-DD:");
        } else if (currentDateRange == null) {
            currentDateRange = message.getText();
            getWeatherData(user.getChatId());
            currentCity = null;
            currentParameter = null;
            currentDateRange = null;
        }
    }

    private void sendParameterButtons(Long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Температура");
        row.add("Давление");
        row.add("Влажность");
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void getWeatherData(Long chatId) {
        if (currentCity != null && currentParameter != null && currentDateRange != null) {
            String url = "http://localhost:8080/weather?city=" + currentCity +
                    "&param=" + currentParameter +
                    "&dateRange=" + currentDateRange;
            try {
                String response = restTemplate.getForObject(url, String.class);
                sendMessage(chatId, response);
            } catch (Exception e) {
                sendMessage(chatId, "Ошибка при получении данных о погоде. Попробуйте позже.");
                e.printStackTrace();
            }
        } else {
            sendMessage(chatId, "Не все данные введены. Пожалуйста, повторите запрос.");
        }
    }

}
