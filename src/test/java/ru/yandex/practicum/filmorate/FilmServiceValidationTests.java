package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceValidationTests {

    private FilmService filmService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());
        filmService = new FilmService(new InMemoryFilmStorage(), userService);
    }

    private Film createFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        return film;
    }

    private User createUser(Long id) {
        User user = new User();
        user.setEmail("mail" + id + "@mail.com");
        user.setLogin("user" + id);
        user.setName("User " + id);
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return userService.addUser(user);
    }

    @Test
    void testAddAndGetFilm() {
        Film film = createFilm("Фильм 1");
        Film saved = filmService.addFilm(film);
        Film retrieved = filmService.getFilmById(saved.getId());
        assertEquals(saved.getId(), retrieved.getId());
    }

    @Test
    void testUpdateFilm() {
        Film saved = filmService.addFilm(createFilm("Фильм 2"));
        saved.setName("Новое имя");
        filmService.updateFilm(saved);
        assertEquals("Новое имя", filmService.getFilmById(saved.getId()).getName());
    }

    @Test
    void testLikesAffectPopularityOrder() {
        Film film1 = filmService.addFilm(createFilm("A"));
        Film film2 = filmService.addFilm(createFilm("B"));

        User u1 = createUser(1L);
        User u2 = createUser(2L);
        User u3 = createUser(3L);

        filmService.addLike(film1.getId(), u1.getId());
        filmService.addLike(film1.getId(), u2.getId());
        filmService.addLike(film2.getId(), u3.getId());

        List<Film> popular = filmService.getPopularFilms(5);

        assertEquals(film1.getId(), popular.get(0).getId());
        assertEquals(film2.getId(), popular.get(1).getId());
    }

    @Test
    void testRemoveLike() {
        Film film = filmService.addFilm(createFilm("Film"));
        User u1 = createUser(1L);
        User u2 = createUser(2L);

        filmService.addLike(film.getId(), u1.getId());
        filmService.addLike(film.getId(), u2.getId());
        filmService.removeLike(film.getId(), u1.getId());

        List<Film> popular = filmService.getPopularFilms(5);
        assertEquals(film.getId(), popular.get(0).getId());
    }
}
