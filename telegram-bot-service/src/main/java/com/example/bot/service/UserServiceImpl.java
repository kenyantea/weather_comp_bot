package com.example.bot.service;

import com.example.bot.model.User;
import com.example.bot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Override
    public User getUserByChatId(Long chatId) {
        Optional<User> userOptional = userRepository.findByChatId(chatId);
        return userOptional.orElse(null);
    }

    @Override
    public User registerNewUser(Long chatId, String name) {
        User user = new User(chatId, name);
        return userRepository.save(user);
    }
}
