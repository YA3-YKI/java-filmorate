package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return jdbcTemplate.queryForList("SELECT * FROM genres ORDER BY id");
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable int id) {
        return jdbcTemplate.queryForMap("SELECT * FROM genres WHERE id = ?", id);
    }
}
