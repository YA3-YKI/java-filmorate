package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getById(id);
    }

    public Film addFilm(Film film) {
        validate(film);
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId); // ✅ Проверка существования пользователя
        film.getLikes().add(user.getId());
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId); // ✅ Проверка существования пользователя
        film.getLikes().remove(user.getId());
    }

    public List<Film> getPopularFilms(int count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count > 0 ? count : 10)
                .collect(Collectors.toList());
    }

    private void validate(Film film) {
        LocalDate earliest = LocalDate.of(1895, 12, 28);

        if (film.getName() == null || film.getName().isBlank())
            throw new ValidationException("Название не может быть пустым");

        if (film.getDescription() != null && film.getDescription().length() > 200)
            throw new ValidationException("Описание больше 200 символов");

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(earliest))
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");

        if (film.getDuration() == null || film.getDuration() <= 0)
            throw new ValidationException("Продолжительность должна быть положительной");
    }
}
