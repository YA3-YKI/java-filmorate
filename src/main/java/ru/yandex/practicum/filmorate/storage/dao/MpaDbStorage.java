package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> findAll() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    public Optional<Mpa> findById(Long id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        List<Mpa> mpas = jdbcTemplate.query(sql, this::mapRowToMpa, id);
        return mpas.isEmpty() ? Optional.empty() : Optional.of(mpas.get(0));
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getLong("id"), rs.getString("name"));
    }
}