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
    private final UserStorage storage;
    private final FeedStorage feedStorage;


    @Override
    public User createUser(User user) {
        return storage.createUser(user);
    }


    @Override
    public User updateUser(User user) {
        getCheckUserThrow(user.getId());
        storage.updateUser(user);
        return user;
    }


    @Override
    public List<User> getUsers() {
        return storage.getUsers();
    }


    @Override
    public User getUserById(Integer id) {
        return getCheckUserThrow(id);
    }


    private User getCheckUserThrow(Integer id) {
        return storage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }


    @Override
    public void addFriend(Integer userId, Integer friendId) {
        User user = getCheckUserThrow(userId);
        User friend = getCheckUserThrow(friendId);
        feedStorage.addEvent(userId, "FRIEND", "ADD", friendId);
        storage.addFriend(user, friend);
    }


    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        User user = getCheckUserThrow(userId);
        User friend = getCheckUserThrow(friendId);
        feedStorage.addEvent(userId, "FRIEND", "REMOVE", friendId);
        storage.removeFriend(user, friend);
    }


    @Override
    public List<User> getFriends(Integer id) {
        User user = getCheckUserThrow(id);
        return storage.getFriends(user);
    }


    @Override
    public List<User> getSameFriends(Integer userId, Integer otherId) {
        User user1 = getCheckUserThrow(userId);
        User other = getCheckUserThrow(otherId);
        return storage.getSameFriend(user1, other);
    }

    @Override
    public List<Film> getUserRecommendations(Integer id) {
        getCheckUserThrow(id);
        return storage.getUserRecommendations(id);
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = storage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        storage.deleteUser(user);
    }

    public List<Event> getFeedByUserId(Integer userId) {
        getCheckUserThrow(userId);
        return feedStorage.getFeedByUserId(userId);
    }
}
