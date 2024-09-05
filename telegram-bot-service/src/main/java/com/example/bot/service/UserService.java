package com.example.bot.service;

import com.example.bot.model.User;

import java.io.IOException;

public interface UserService {
    User getUserByChatId(Long chatId) throws IOException;
    User registerNewUser(Long chatId, String name) throws IOException;
}
