package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;


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
        storage.addFriend(user, friend);
    }


    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        User user = getCheckUserThrow(userId);
        User friend = getCheckUserThrow(friendId);
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
}
