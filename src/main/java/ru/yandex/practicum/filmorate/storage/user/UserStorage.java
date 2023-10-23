package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    void updateUser(User user);

    List<User> getUsers();

    Optional<User> getUserById(int id);

    void addFriend(User user, User friend);

    void removeFriend(User user, User friend);

    List<User> getFriends(User user);

    List<User> getSameFriend(User user, User otherUser);
}
