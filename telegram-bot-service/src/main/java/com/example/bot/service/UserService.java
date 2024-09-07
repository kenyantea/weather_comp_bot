package com.example.bot.service;

import com.example.bot.model.User;
import org.springframework.stereotype.Service;
@Service
public interface UserService {

    User getUserByChatId(Long chatId);
    User registerNewUser(Long chatId, String name);
}
