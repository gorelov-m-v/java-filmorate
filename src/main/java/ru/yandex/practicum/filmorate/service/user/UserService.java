package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user);

    List<User> getUsers();

    User getUserById(Integer id);

    void addFriend(Integer userId, Integer friendId);

    void removeFriend(Integer userId, Integer friendId);

    List<User> getFriends(Integer id);

    List<User> getSameFriends(Integer userId, Integer friendId);

    List<Film> getUserRecommendations(Integer id);

    void deleteUser(Integer userId);
}
