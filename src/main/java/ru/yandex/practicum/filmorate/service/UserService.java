package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.findAll();
    }

    public User getUserById(Long id) {
        return userStorage.getById(id);
    }

    public User addUser(User user) {
        log.info("Добавление пользователя: {}", user.getLogin());
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Добавление в друзья: {} → {}", userId, friendId);

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().put(friendId, FriendshipStatus.UNCONFIRMED);

        friend.getFriends().put(userId, FriendshipStatus.CONFIRMED);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Long userId) {
        User user = getUserById(userId);

        return user.getFriends().keySet().stream()
                .map(this::getUserById)
                .toList();
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {

        Set<Long> friends1 = getUserById(userId).getFriends().keySet();
        Set<Long> friends2 = getUserById(otherId).getFriends().keySet();

        return friends1.stream()
                .filter(friends2::contains)
                .map(this::getUserById)
                .toList();
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@"))
            throw new ValidationException("Email некорректен");
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" "))
            throw new ValidationException("Логин некорректен: пробелы недопустимы");
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now()))
            throw new ValidationException("Дата рождения не может быть в будущем");
    }
}