package com.example.bot.service;

import com.example.bot.model.User;
import com.example.bot.BotConfig;
import com.example.bot.model.WeatherResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static org.jfree.chart.ChartUtils.saveChartAsPNG;

@Service
public class BotService extends TelegramLongPollingBot {

    private UserService userService;
    private RestTemplate restTemplate;
    private BotConfig botConfiguration;

    private String currentCity;
    private String currentParameter;

    private String currentStartDate;
    private String currentEndDate;
    private String currentFrequency;
    private String currentDay;
    private static final String format = "yyyy-MM-dd";
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
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
            if (update.getMessage().getText().equals("/start")) {
                if (user == null) {
                    user = userService.registerNewUser(chatId, userName);
                    sendMessage(chatId,"Nice to meet you, " + userName + "! :)");
                } else {
                    sendMessage(chatId,"Nice to see you again, " + userName + "! :)");
                }
                sendMessage(chatId, "I'm a little bot intended for some weather analyzing 🌈");
                sendMessage(chatId, "What town we're gonna talk about?");
            } else {
                processUserInput(message, user);
            }
        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            User user = userService.getUserByChatId(chatId);
            if (call_data.equals("temp") || call_data.equals("humidity")
                    || call_data.equals("feelslike") || call_data.equals("windspeed")) {
                currentParameter = call_data;
                if (currentFrequency.equals("yearly")) {
                    sendMessage(user.getChatId(), "Enter the day you want to know about using the following format: \nMM-DD");
                } else {
                    sendMessage(user.getChatId(), "Enter the start date using the following format: \nYYYY-MM-DD:");
                }
            } else if (call_data.equals("daily") || call_data.equals("yearly")) {
                currentFrequency = call_data;
                sendParameterButtons(chatId);
            }
        }
    }

    private void processUserInput(Message message, User user) {
        if (currentCity == null) {
            currentCity = message.getText();
            sendFrequencyButtons(user.getChatId());
        } else if (currentParameter == null) {
            sendParameterButtons(user.getChatId());
        } else if (currentFrequency.equals("daily")) {
            if (currentStartDate == null) {
                currentStartDate = message.getText();
                if (isValidDate(currentStartDate, format)) {
                    sendMessage(user.getChatId(), "Enter the end date using the following format: \nYYYY-MM-DD:");
                } else {
                    currentStartDate = null;
                    sendMessage(user.getChatId(), "The format is incorrect. Let's try again :)");
                    sendMessage(user.getChatId(), "Enter the start date using the following format: \nYYYY-MM-DD:");
                }
            } else if (currentEndDate == null) {
                currentEndDate = message.getText();
                if (isValidDate(currentStartDate, format)) {
                    if (startBeforeEnd(currentStartDate, currentEndDate)) {
                        getWeatherData(user.getChatId());
                        currentCity = null;
                        currentParameter = null;
                    } else {
                        sendMessage(user.getChatId(), "The end date is before the start date. Let's try again :)");
                        sendMessage(user.getChatId(), "Enter the start date using the following format: \nYYYY-MM-DD:");
                    }
                    currentStartDate = null;
                    currentEndDate = null;
                } else {
                    currentEndDate = null;
                    sendMessage(user.getChatId(), "The format is incorrect. Let's try again :)");
                    sendMessage(user.getChatId(), "Enter the end date using the following format: \nYYYY-MM-DD:");
                }
            }
        } else if (currentFrequency.equals("yearly")) {
            if (currentDay == null) {
                String temp = "2024-";
                currentDay = message.getText();
                temp += currentDay;
                if (isValidDate(temp, format)){
                    sendMessage(user.getChatId(), "Enter the start date using the following format: \nYYYY");
                } else {
                    currentDay = null;
                    sendMessage(user.getChatId(), "The format is incorrect. Let's try again :)");
                    sendMessage(user.getChatId(), "Enter the day you want to know about using the following format: \nMM-DD");
                }

            } else if (currentStartDate == null) {
                currentStartDate = message.getText();
                if (isValidYear(currentStartDate)) {
                    sendMessage(user.getChatId(), "Enter the end date using the following format: \nYYYY:");
                } else {
                    currentStartDate = null;
                    sendMessage(user.getChatId(), "According to the API I use, the year can't be less " +
                            "than 50 years and greater than this year. Let's try again :)");
                    sendMessage(user.getChatId(), "Enter the start date using the following format: \nYYYY:");
                }
            } else if (currentEndDate == null) {
                currentEndDate = message.getText();
                if (isValidYear(currentEndDate)) {
                    if (Integer.parseInt(currentStartDate) <= Integer.parseInt(currentEndDate)) {
                        getWeatherData(user.getChatId());
                        currentCity = null;
                        currentParameter = null;
                    } else {
                        sendMessage(user.getChatId(), "The end year is before the start year. Let's try again :)");
                        sendMessage(user.getChatId(), "Enter the start date using the following format: \nYYYY:");
                    }
                    currentStartDate = null;
                    currentEndDate = null;
                } else {
                    currentEndDate = null;
                    sendMessage(user.getChatId(), "According to the API I use, the year can't be less " +
                            "than 50 years and greater than this year. Let's try again :)");
                    sendMessage(user.getChatId(), "Enter the end date using the following format: \nYYYY:");
                }
            }
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
        inlineKeyboardButton1.setCallbackData("temp");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Humidity");
        inlineKeyboardButton2.setCallbackData("humidity");
        rowInline1.add(inlineKeyboardButton1);
        rowInline1.add(inlineKeyboardButton2);

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        inlineKeyboardButton3.setText("Feels like");
        inlineKeyboardButton3.setCallbackData("feelslike");
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        inlineKeyboardButton4.setText("Wind speed");
        inlineKeyboardButton4.setCallbackData("windspeed");
        rowInline2.add(inlineKeyboardButton3);
        rowInline2.add(inlineKeyboardButton4);

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendFrequencyButtons(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Should I consider every day in the period (daily) or only the chosen day (yearly)?");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Yearly");
        inlineKeyboardButton1.setCallbackData("yearly");
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton2.setText("Daily");
        inlineKeyboardButton2.setCallbackData("daily");
        rowInline1.add(inlineKeyboardButton1);
        rowInline1.add(inlineKeyboardButton2);

        rowsInline.add(rowInline1);

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

    public static boolean isValidDate(String dateString, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        try {
            LocalDate.parse(dateString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidYear(String yearString) {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        try {
            int inputYear = Integer.parseInt(yearString);
            int minYear = currentYear - 50; //не больше чем на 50 лет назад согласно API
            return inputYear >= minYear && inputYear <= currentYear;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean startBeforeEnd(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        return start.isBefore(end) || start.isEqual(end);
    }

    private void getWeatherData(Long chatId) {
        if (currentCity != null && currentParameter != null && currentStartDate != null
                && currentEndDate != null && currentFrequency != null
                && (currentFrequency.equals("yearly") && currentDay != null
                || currentFrequency.equals("daily"))) {
            String body = switch(currentFrequency) {
                case "yearly" -> "http://weather-service:8081/weather/v1/process_yearly?day=" + currentDay + "&city=";
                case "daily" -> "http://weather-service:8081/weather/v1/process_daily?city=";
                default -> "";
            };
            String url = body + currentCity + "&param=" + currentParameter +
                    "&start=" + currentStartDate + "&end=" + currentEndDate;
            try {
                String response = restTemplate.getForObject(url, String.class);
                responseToMessage(chatId, response);
                sendMessage(chatId,"Wanna ask me more? Type /start :)");
            } catch (Exception e) {
                sendMessage(chatId, "There was an error getting the weather data. Press /start to try again later!");
                e.printStackTrace();
            }
        } else {
            sendMessage(chatId, "There's some data missing. Press /start to try again!");
            sendMessage(chatId, "What town we're gonna talk about?");
        }
        currentCity = null;
        currentParameter = null;
        currentStartDate = null;
        currentEndDate = null;
        currentFrequency = null;
        currentDay = null;
    }

    public void responseToMessage(Long chatId, String resp) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            WeatherResponse response = objectMapper.readValue(resp, WeatherResponse.class);
            String param = switch (currentParameter) {
                case "temp" -> "temperature";
                case "humidity" -> "humidity";
                case "windspeed" -> "wind speed";
                case "feelslike" -> "feels like";
                default -> "";
            };
            String answer = getAnswerString(param, response);
            sendMessage(chatId,answer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private String getAnswerString(@NotNull String param, WeatherResponse response) {
        if (response.error != null) {
            return "Error: " + response.error;
        }

        String answer = "";
        if (currentDay != null && !currentDay.isEmpty()) {
            answer += "Day: " + currentDay + "\n";
        }
        answer += "Date range: " + currentStartDate + " to " + currentEndDate + "\n"
                + "City: " + currentCity + "\n"
                + "Parameter: " + param + "\n"
                + "Minimum: " + response.min + "\n"
                + "Maximum: " + response.max + "\n"
                + "Average: " + response.average + "\n"
                + "Total proceeded days: " + response.totalParameters;
        return answer;
    }

    public void generateGraph(Long chatId) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(10, currentParameter, "2023-10-26");
        dataset.addValue(12, currentParameter, "2023-10-27");
        dataset.addValue(15, currentParameter, "2023-10-28");

        JFreeChart chart = ChartFactory.createLineChart(
                "The graph generated by @weather_comp_bot", // Заголовок графика
                "Date", // Метка оси X
                currentParameter, // Метка оси Y
                dataset, PlotOrientation.VERTICAL, // Ориентация (вертикальная)
                true, // Включить легенду
                true, // Включить подсказки
                false // Включить URL-ссылки
        );

        // Сохраняем график в файл
        File chartFile = new File("temperature_chart.png");
        try {
            saveChartAsPNG(chartFile, chart, 500, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            sendPhoto("temperature_chart.png", chatId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPhoto(String photoUrl, Long chatId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("chat_id", chatId.toString())
                .addFormDataPart("photo", "temperature_chart.jpg", RequestBody.create(MediaType.parse("image/jpeg"), new File(photoUrl)))
                .build();

        Request request = new Request.Builder()
                .url("https://api.telegram.org/bot" + getBotToken() + "/sendPhoto")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            System.out.println(response.body().string());
        }
    }
}
