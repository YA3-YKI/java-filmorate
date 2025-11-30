package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return jdbcTemplate.queryForList("SELECT * FROM mpa ORDER BY id");
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable int id) {
        return jdbcTemplate.queryForMap("SELECT * FROM mpa WHERE id = ?", id);
    }
}
