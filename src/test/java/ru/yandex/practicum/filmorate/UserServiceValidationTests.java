package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceValidationTests {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());
    }

    private User createUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(login);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    @Test
    void testAddAndGetUser() {
        User saved = userService.addUser(createUser("test@mail.com", "user1"));
        assertEquals(saved.getId(), userService.getUserById(saved.getId()).getId());
    }

    @Test
    void testUpdateUser() {
        User saved = userService.addUser(createUser("u@mail.com", "u1"));
        saved.setName("Новое Имя");
        userService.updateUser(saved);

        assertEquals("Новое Имя", userService.getUserById(saved.getId()).getName());
    }

    @Test
    void testAddAndRemoveFriends() {
        User u1 = userService.addUser(createUser("1@mail.com", "u1"));
        User u2 = userService.addUser(createUser("2@mail.com", "u2"));

        userService.addFriend(u1.getId(), u2.getId());

        List<User> friendsOfU1 = userService.getFriends(u1.getId());
        assertEquals(1, friendsOfU1.size());
        assertEquals(u2.getId(), friendsOfU1.get(0).getId());

        userService.removeFriend(u1.getId(), u2.getId());
        assertTrue(userService.getFriends(u1.getId()).isEmpty());
    }

    @Test
    void testCommonFriends() {
        User u1 = userService.addUser(createUser("1@mail.com", "u1"));
        User u2 = userService.addUser(createUser("2@mail.com", "u2"));
        User u3 = userService.addUser(createUser("3@mail.com", "u3"));

        userService.addFriend(u1.getId(), u3.getId());
        userService.addFriend(u2.getId(), u3.getId());

        List<User> common = userService.getCommonFriends(u1.getId(), u2.getId());

        assertEquals(1, common.size());
        assertEquals(u3.getId(), common.getFirst().getId());
    }
}
