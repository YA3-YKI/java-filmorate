package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public ResponseEntity<Collection<User>> findAll() {
        log.info("Получен запрос: GET /users (список всех пользователей)");
        return ResponseEntity.ok(users.values());
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        log.info("Получен запрос: POST /users — создание пользователя с email {}", user.getEmail());
        if (!userValidation(user)) {
            log.warn("Ошибка валидации при создании пользователя: {}", user);
            throw new ValidationException("Ошибка заполнения данных");
        }
        // формируем дополнительные данные
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не указано, используется login='{}' как имя", user.getLogin());
        }
        // сохраняем новую публикацию в памяти приложения
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан: id={}, login={}", user.getId(), user.getLogin());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping
    public ResponseEntity<User> update(@RequestBody User newUser) {
        log.info("Получен запрос: PUT /users — обновление пользователя id={}", newUser.getId());
        if (newUser.getId() == null) {
            log.error("Попытка обновления пользователя без ID");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (!userValidation(newUser)) {
                log.warn("Ошибка валидации при обновлении пользователя: {}", newUser);
                throw new ValidationException("Ошибка заполнения данных");
            }
            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }
            log.info("Пользователь успешно обновлён: id={}, login={}", newUser.getId(), newUser.getLogin());
            return ResponseEntity.ok(oldUser);
        }
        log.warn("Пользователь с id={} не найден для обновления", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public boolean userValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            return false;
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            return false;
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            return false;
        }


        return true;
    }
}