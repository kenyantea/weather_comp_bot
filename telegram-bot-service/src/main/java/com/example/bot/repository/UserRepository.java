package com.example.bot.repository;

import com.example.bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    java.util.Optional<User> findByChatId(Long chatId);
}

