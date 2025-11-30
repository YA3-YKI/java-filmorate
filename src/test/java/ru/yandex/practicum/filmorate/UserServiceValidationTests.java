package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserServiceValidationTests {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());
    }

    @Test
    void testAddUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User savedUser = userService.addUser(user);
        assertNotNull(savedUser.getId());
        assertEquals("testLogin", savedUser.getLogin());
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User");
        user.setBirthday(LocalDate.of(1995, 5, 15));

        User savedUser = userService.addUser(user);
        savedUser.setName("Updated Name");

        User updatedUser = userService.updateUser(savedUser);
        assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("a@example.com");
        user1.setLogin("aLogin");
        user1.setName("A");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("b@example.com");
        user2.setLogin("bLogin");
        user2.setName("B");
        user2.setBirthday(LocalDate.of(1992, 2, 2));

        userService.addUser(user1);
        userService.addUser(user2);

        assertEquals(2, userService.getAllUsers().size());
    }
}