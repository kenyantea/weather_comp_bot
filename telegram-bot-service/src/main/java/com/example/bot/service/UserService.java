package com.example.bot.service;

import com.example.bot.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private Map<Long, User> userDatabase = new HashMap<>();

    public User getUserByChatId(Long chatId) {
        return userDatabase.get(chatId);
    }

    public User registerNewUser(Long chatId, String name) {
        User user = new User();
        user.setChatId(chatId);
        user.setName(name);
        userDatabase.put(chatId, user);
        return user;
    }
}
