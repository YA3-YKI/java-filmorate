package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен запрос: GET /films (список всех фильмов)");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Получен запрос: POST /films — создание фильма {}", film.getName());

        if (!filmsValidation(film)) {
            log.warn("Ошибка валидации при создании фильма: {}", film);
            throw new ValidationException("Ошибка заполнения данных");
        }
        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новую публикацию в памяти приложения
        films.put(film.getId(), film);
        log.info("Фильм успешно создан: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Получен запрос: PUT /films — обновление фильма id={}", newFilm.getId());
        if (newFilm.getId() == null) {
            log.error("Попытка обновления без ID");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (!filmsValidation(newFilm)) {
                log.warn("Ошибка валидации при обновлении фильма: {}", newFilm);
                throw new ValidationException("Ошибка заполнения данных");
            }
            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Фильм успешно обновлён: id={}, name={}", newFilm.getId(), newFilm.getName());
            return oldFilm;
        }
        log.warn("Фильм с id={} не найден для обновления", newFilm.getId());
        throw new NotFoundException("Пост с id = " + newFilm.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public boolean filmsValidation(Film film) {
        LocalDate earliestDate = LocalDate.of(1895, 12, 28);

        if (film.getName() == null || film.getName().isBlank()) {
            return false;
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            return false;
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(earliestDate)) {
            return false;
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            return false;
        }

        return true;
    }
}