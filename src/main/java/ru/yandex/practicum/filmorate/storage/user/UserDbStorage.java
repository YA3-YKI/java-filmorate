package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    };

    @Override
    public User addUser(User user) {
        jdbcTemplate.update(
                "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday()
        );
        Long id = jdbcTemplate.queryForObject("SELECT id FROM users WHERE email = ?", Long.class, user.getEmail());
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        int updated = jdbcTemplate.update(
                "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId()
        );
        if (updated == 0) throw new NotFoundException("User not found");
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE id = ?", userRowMapper, id
        );
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", userRowMapper);
    }
}