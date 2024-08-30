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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
        handleUpdate(update);
    }

    public void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String userName = message.getFrom().getFirstName();

            User user = userService.getUserByChatId(chatId);
            if (user == null) {
                user = userService.registerNewUser(chatId, userName);
                sendMessage(chatId,"Nice to meet you, " + userName + "! :)");
            } else {
                sendMessage(chatId,"Nice to see you again, " + userName + "! :)");
            } if (update.getMessage().getText().equals("/start")) {
                    sendMessage(chatId, "I'm a little bot intended for some weather analyzing 🌈");
                    sendMessage(chatId, "What town we're gonna talk about?");
                } else {
                processUserInput(message, user);
            }
        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            User user = userService.getUserByChatId(chatId);
            if (call_data.equals("Temperature")) {
                currentParameter = "Temperature";
            } else if (call_data.equals("Humidity")) {
                currentParameter = "Humidity";
            }
            sendMessage(user.getChatId(), "Enter the dates using the following format: \nYYYY-MM-DD YYYY-MM-DD:");
        }
    }

    private void processUserInput(Message message, User user) {
        if (currentCity == null) {
            currentCity = message.getText();
            sendParameterButtons(user.getChatId());
        } else if (currentDateRange == null) {
            currentDateRange = message.getText();
            getWeatherData(user.getChatId());
            currentCity = null;
            currentParameter = null;
            currentDateRange = null;
        }
    }

    private void sendParameterButtons(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose one of the following parameters:");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Temperature");
        inlineKeyboardButton1.setCallbackData("Temperature");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Humidity");
        inlineKeyboardButton2.setCallbackData("Humidity");
        rowInline1.add(inlineKeyboardButton1);
        rowInline1.add(inlineKeyboardButton2);

//        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
//        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
//        inlineKeyboardButton3.setText("Амелин");
//        inlineKeyboardButton3.setCallbackData("АМЕЛИН");
//        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
//        inlineKeyboardButton4.setText("Арстер");
//        inlineKeyboardButton4.setCallbackData("АРСТЕР");
//        rowInline2.add(inlineKeyboardButton3);
//        rowInline2.add(inlineKeyboardButton4);

        rowsInline.add(rowInline1);
//        rowsInline.add(rowInline2);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

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
                sendMessage(chatId, "There was an error getting the weather data. Try again later!");
                e.printStackTrace();
            }
        } else {
            sendMessage(chatId, "There's some data missing. Let's try again!");
            sendMessage(chatId, "What town we're gonna talk about?");
            currentCity = null;
            currentParameter = null;
            currentDateRange = null;

        }
    }

}
