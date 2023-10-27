package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    @Override
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        getCheckUserThrow(user.getId());
        userStorage.updateUser(user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public User getUserById(Integer id) {
        return getCheckUserThrow(id);
    }

    private User getCheckUserThrow(Integer id) {
        return userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        User user = getCheckUserThrow(userId);
        User friend = getCheckUserThrow(friendId);
        feedStorage.addEvent(userId, "FRIEND", "ADD", friendId);
        userStorage.addFriend(user, friend);
    }

    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        User user = getCheckUserThrow(userId);
        User friend = getCheckUserThrow(friendId);
        feedStorage.addEvent(userId, "FRIEND", "REMOVE", friendId);
        userStorage.removeFriend(user, friend);
    }

    @Override
    public List<User> getFriends(Integer id) {
        User user = getCheckUserThrow(id);
        return userStorage.getFriends(user);
    }

    @Override
    public List<User> getSameFriends(Integer userId, Integer otherId) {
        User user1 = getCheckUserThrow(userId);
        User other = getCheckUserThrow(otherId);
        return userStorage.getSameFriend(user1, other);
    }

    @Override
    public List<Film> getUserRecommendations(Integer id) {
        getCheckUserThrow(id);
        return userStorage.getUserRecommendations(id);
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        userStorage.deleteUser(user);
    }

    public List<Event> getFeedByUserId(Integer userId) {
        getCheckUserThrow(userId);
        return feedStorage.getFeedByUserId(userId);
    }
}
