package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        user.setId(idCounter++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (user != null && friend != null) {
            user.getFriends().add(friendId);
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        if (user != null) {
            user.getFriends().remove(friendId);
        }
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = users.get(userId);
        if (user == null) return List.of();

        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            User friend = users.get(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Long userId1, Long userId2) {
        User user1 = users.get(userId1);
        User user2 = users.get(userId2);
        if (user1 == null || user2 == null) return List.of();

        Set<Long> commonIds = new HashSet<>(user1.getFriends());
        commonIds.retainAll(user2.getFriends());

        List<User> commonFriends = new ArrayList<>();
        for (Long id : commonIds) {
            User friend = users.get(id);
            if (friend != null) {
                commonFriends.add(friend);
            }
        }
        return commonFriends;
    }
}