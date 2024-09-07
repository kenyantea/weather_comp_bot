package com.example.bot.service;

import com.example.bot.model.User;
import com.example.bot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserByChatId_ShouldReturnUser() {
        // Настройка
        Long chatId = 123456L;
        User expectedUser = new User(chatId, "User Name");
        when(userRepository.findByChatId(chatId)).thenReturn(Optional.of(expectedUser));

        User user = userServiceImpl.getUserByChatId(chatId);

        assertNotNull(user);
        assertEquals(expectedUser, user);
        verify(userRepository).findByChatId(chatId);
    }

    @Test
    void testGetUserByChatId_ShouldReturnNull() {
        Long chatId = 123456L;
        when(userRepository.findByChatId(chatId)).thenReturn(Optional.empty());

        User user = userServiceImpl.getUserByChatId(chatId);

        assertNull(user);
        verify(userRepository).findByChatId(chatId);
    }

    @Test
    void testRegisterNewUser_ShouldSaveUser() {
        Long chatId = 123456L;
        String name = "User Name";
        User expectedUser = new User(chatId, name);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        User user = userServiceImpl.registerNewUser(chatId, name);

        assertNotNull(user);
        assertEquals(expectedUser, user);
        verify(userRepository).save(any(User.class));
    }
}
