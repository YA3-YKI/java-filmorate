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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmServiceValidationTests {

    private FilmService filmService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserStorage());
        filmService = new FilmService(new InMemoryFilmStorage(), userService);
    }

    @Test
    void testAddAndGetFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film savedFilm = filmService.addFilm(film);
        Film retrievedFilm = filmService.getFilmById(savedFilm.getId());

        assertEquals(savedFilm.getName(), retrievedFilm.getName());
        assertEquals(savedFilm.getDuration(), retrievedFilm.getDuration());
    }

    @Test
    void testAddLikeAndPopularFilms() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("User");
        user.setBirthday(LocalDate.of(1995, 5, 15));
        User savedUser = userService.addUser(user);

        Film film1 = new Film();
        film1.setName("Film1");
        film1.setDescription("Desc1");
        film1.setReleaseDate(LocalDate.of(2001, 1, 1));
        film1.setDuration(100);
        film1 = filmService.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("Desc2");
        film2.setReleaseDate(LocalDate.of(2002, 2, 2));
        film2.setDuration(120);
        film2 = filmService.addFilm(film2);

        filmService.addLike(film1.getId(), savedUser.getId());

        List<Film> popularFilms = filmService.getPopularFilms(2);
        assertEquals(film1.getId(), popularFilms.get(0).getId());
    }

    @Test
    void testRemoveLike() {
        User user = new User();
        user.setEmail("user2@example.com");
        user.setLogin("login2");
        user.setName("User2");
        user.setBirthday(LocalDate.of(1990, 3, 3));
        User savedUser = userService.addUser(user);

        Film film = new Film();
        film.setName("FilmX");
        film.setDescription("DescX");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);
        film = filmService.addFilm(film);

        filmService.addLike(film.getId(), savedUser.getId());
        filmService.removeLike(film.getId(), savedUser.getId());

        assertTrue(filmService.getFilmById(film.getId()).getLikes().isEmpty());
    }
}