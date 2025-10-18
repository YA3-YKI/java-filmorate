package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserValidationTests {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void testValidUser() {
        User user = new User();
        user.setEmail("ivan@example.com");
        user.setLogin("ivan123");
        user.setName("Иван");
        user.setBirthday(LocalDate.of(1980, 5, 10));

        assertTrue(userController.userValidation(user));
    }

    @Test
    void testEmptyEmail() {
        User user = new User();
        user.setEmail("");
        user.setLogin("ivan123");
        user.setBirthday(LocalDate.of(1980, 5, 10));

        assertFalse(userController.userValidation(user));
    }

    @Test
    void testEmailWithoutAt() {
        User user = new User();
        user.setEmail("ivan.example.com");
        user.setLogin("ivan123");
        user.setBirthday(LocalDate.of(1980, 5, 10));

        assertFalse(userController.userValidation(user));
    }

    @Test
    void testLoginWithSpaces() {
        User user = new User();
        user.setEmail("ivan@example.com");
        user.setLogin("ivan 123");
        user.setBirthday(LocalDate.of(1980, 5, 10));

        assertFalse(userController.userValidation(user));
    }

    @Test
    void testBirthdayInFuture() {
        User user = new User();
        user.setEmail("ivan@example.com");
        user.setLogin("ivan123");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertFalse(userController.userValidation(user));
    }

    @Test
    void testEmptyLogin() {
        User user = new User();
        user.setEmail("ivan@example.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(1980, 5, 10));

        assertFalse(userController.userValidation(user));
    }
}
