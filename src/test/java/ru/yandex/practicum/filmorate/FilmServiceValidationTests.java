package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceValidationTests {

    private FilmService filmService;

    @BeforeEach
    void setUp() {
        filmService = new FilmService(new InMemoryFilmStorage());
    }

    private Film createFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        return film;
    }

    @Test
    void testAddAndGetFilm() {
        Film film = createFilm("Фильм 1");
        Film saved = filmService.addFilm(film);

        Film retrieved = filmService.getFilmById(saved.getId());

        assertEquals(saved.getId(), retrieved.getId());
        assertEquals("Фильм 1", retrieved.getName());
    }

    @Test
    void testUpdateFilm() {
        Film film = createFilm("Фильм 2");
        Film saved = filmService.addFilm(film);

        saved.setName("Фильм 2 обновленный");
        filmService.updateFilm(saved);

        Film updated = filmService.getFilmById(saved.getId());
        assertEquals("Фильм 2 обновленный", updated.getName());
    }

    @Test
    void testLikesAffectPopularityOrder() {
        Film film1 = filmService.addFilm(createFilm("A"));
        Film film2 = filmService.addFilm(createFilm("B"));

        filmService.addLike(film1.getId(), 1L);
        filmService.addLike(film1.getId(), 2L);
        filmService.addLike(film2.getId(), 3L);

        List<Film> popular = filmService.getPopularFilms(5);

        assertEquals(film1.getId(), popular.get(0).getId());
        assertEquals(film2.getId(), popular.get(1).getId());
    }

    @Test
    void testRemoveLike() {
        Film film = filmService.addFilm(createFilm("Film"));
        filmService.addLike(film.getId(), 1L);
        filmService.addLike(film.getId(), 2L);

        filmService.removeLike(film.getId(), 1L);

        List<Film> popular = filmService.getPopularFilms(5);
        assertEquals(film.getId(), popular.get(0).getId());
    }
}