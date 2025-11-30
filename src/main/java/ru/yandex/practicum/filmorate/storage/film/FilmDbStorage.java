package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        return film;
    };

    @Override
    public Film addFilm(Film film) {
        jdbcTemplate.update(
                "INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration()
        );
        Long id = jdbcTemplate.queryForObject("SELECT id FROM films WHERE name = ?", Long.class, film.getName());
        film.setId(id);
        film.setLikes(new HashSet<>());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int updated = jdbcTemplate.update(
                "UPDATE films SET name=?, description=?, release_date=?, duration=? WHERE id=?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId()
        );
        if (updated == 0) throw new NotFoundException("Film not found");
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM films WHERE id = ?", filmRowMapper, id
        );
    }

    @Override
    public Collection<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT * FROM films", filmRowMapper);
    }
}
