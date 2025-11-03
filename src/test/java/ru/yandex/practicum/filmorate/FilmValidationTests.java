package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmValidationTests {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void testValidFilm() {
        Film film = new Film();
        film.setName("Классика");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1946, 8, 20));
        film.setDuration(120);

        assertTrue(filmController.filmsValidation(film));
    }

    @Test
    void testEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1946, 8, 20));
        film.setDuration(100);

        assertFalse(filmController.filmsValidation(film));
    }

    @Test
    void testDescriptionTooLong() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(1946, 8, 20));
        film.setDuration(100);

        assertFalse(filmController.filmsValidation(film));
    }

    @Test
    void testReleaseDateBefore1895() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(100);

        assertFalse(filmController.filmsValidation(film));
    }

    @Test
    void testNonPositiveDuration() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1946, 8, 20));
        film.setDuration(0);

        assertFalse(filmController.filmsValidation(film));
    }
}