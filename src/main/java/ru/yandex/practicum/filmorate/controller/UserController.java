package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;


    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрос на добавление пользователя");
        User user1 = service.createUser(user);
        log.info("Пользователь создан");
        return user1;

    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление пользователя - " + user.getId());
        if (user.getId() == null) throw new ValidationException("Значение id не может равняться null");
        User user1 = service.updateUser(user);
        log.info("Пользователь " + user1.getId() + " обновлен");
        return user1;
    }


    @GetMapping
    public List<User> getUsers() {
        log.info("Запрос на получение списка всех пользователей");
        List<User> users = service.getUsers();
        log.info("Список всех пользователей отправлен");
        return users;
    }


    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        log.info("Запрос на получение пользователя с id - " + id);
        User user = service.getUserById(id);
        log.info("Пользователь с id - " + id + " отправлен");
        return user;
    }


    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer id,
                          @PathVariable("friendId") Integer friendId) {
        log.info("Запрос на добавление в друзья пользователей: " + id + " и " + friendId);
        service.addFriend(id, friendId);
        log.info("Пользователи: " + id + " и " + friendId + " добавлены в друзья");
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id,
                             @PathVariable Integer friendId) {
        log.info("Запрос на удаление из друзей пользователей: " + id + " и " + friendId);
        service.removeFriend(id, friendId);
        log.info("Пользователи: " + id + " и " + friendId + " удалены из друзей");
    }


    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        log.info("Запрос на получение друзей пользователя - " + id);
        List<User> friends = service.getFriends(id);
        log.info("Отправлен список друзей пользователя - " + id);
        return friends;
    }


    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getSameFriends(@PathVariable Integer id,
                                     @PathVariable Integer otherId) {
        log.info("Запрос на получение общих друзей пользователей: " + id + " и " + otherId);
        List<User> friends = service.getSameFriends(id, otherId);
        log.info("Отправлен список общих друзей пользователей: " + id + " и " + otherId);
        return friends;
    }
}
