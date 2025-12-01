package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper());
        loadGenresForFilms(films);
        loadLikesForFilms(films);
        return films;
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
                "WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), id);
        if (films.isEmpty()) return Optional.empty();

        Film film = films.get(0);
        loadGenresForFilm(film);
        loadLikesForFilm(film);
        return Optional.of(film);
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("mpa_rating_id", film.getMpa() != null ? film.getMpa().getId() : null);

        Long id = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        film.setId(id);
        saveGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, " +
                "mpa_rating_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        saveGenres(film);
        return film;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "MERGE INTO film_likes (film_id, user_id) KEY(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, m.name AS mpa_name, COUNT(fl.user_id) AS likes_count " +
                "FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "GROUP BY f.id, m.name " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), count);
        loadGenresForFilms(films);
        loadLikesForFilms(films);
        return films;
    }

    private void saveGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            List<Object[]> batchArgs = film.getGenres().stream()
                    .map(genre -> new Object[]{film.getId(), genre.getId()})
                    .collect(Collectors.toList());
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }

    private void loadGenresForFilm(Film film) {
        String sql = "SELECT g.id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ? ORDER BY g.id";
        List<Genre> genres = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name")),
                film.getId());
        film.setGenres(new HashSet<>(genres));
    }

    private void loadGenresForFilms(List<Film> films) {
        if (films.isEmpty()) return;
        Map<Long, Film> filmMap = films.stream().collect(Collectors.toMap(Film::getId, f -> f));

        String inClause = String.join(",", Collections.nCopies(films.size(), "?"));
        String sql = String.format(
                "SELECT fg.film_id, g.id, g.name FROM film_genres fg " +
                        "JOIN genres g ON fg.genre_id = g.id " +
                        "WHERE fg.film_id IN (%s)", inClause);

        List<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        jdbcTemplate.query(sql, filmIds.toArray(), rs -> {
            Long filmId = rs.getLong("film_id");
            Film film = filmMap.get(filmId);
            if (film != null) {
                film.getGenres().add(new Genre(rs.getLong("id"), rs.getString("name")));
            }
        });
    }

    private void loadLikesForFilm(Film film) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.queryForList(sql, Long.class, film.getId());
        film.setLikes(new HashSet<>(likes));
    }

    private void loadLikesForFilms(List<Film> films) {
        if (films.isEmpty()) return;
        Map<Long, Film> filmMap = films.stream().collect(Collectors.toMap(Film::getId, f -> f));

        String inClause = String.join(",", Collections.nCopies(films.size(), "?"));
        String sql = String.format("SELECT film_id, user_id FROM film_likes WHERE film_id IN (%s)", inClause);

        List<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        jdbcTemplate.query(sql, filmIds.toArray(), rs -> {
            Long filmId = rs.getLong("film_id");
            Long userId = rs.getLong("user_id");
            Film film = filmMap.get(filmId);
            if (film != null) {
                film.getLikes().add(userId);
            }
        });
    }

    private static class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Film.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getInt("duration"))
                    .mpa(rs.getLong("mpa_rating_id") > 0 ?
                            new Mpa(rs.getLong("mpa_rating_id"), rs.getString("mpa_name")) : null)
                    .genres(new HashSet<>())
                    .likes(new HashSet<>())
                    .build();
        }
    }
}