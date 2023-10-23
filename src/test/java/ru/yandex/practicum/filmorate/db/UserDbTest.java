package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbTest {
    private final UserDbStorage userStorage;

    @BeforeEach
    public void createParams() {
        User user = User.builder()
                .name("name")
                .login("login")
                .email("email@email.ru")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();
        userStorage.createUser(user);
    }

    @Test
    public void shouldUpdate() {
        User user = new User(1, "email@email.ru", "new", "new",
                LocalDate.of(2001, 1, 1));
        userStorage.updateUser(user);
        User user1 = userStorage.getUserById(1).orElseThrow();

        assertEquals(user.getId(), user1.getId());
        assertEquals(user.getEmail(), user1.getEmail());
        assertEquals(user.getLogin(), user1.getLogin());
        assertEquals(user.getName(), user1.getName());
        assertEquals(user.getBirthday(), user1.getBirthday());
    }

    @Test
    public void shouldGetAllUsers() {
        assertEquals(1, userStorage.getUsers().size());
    }

    @Test
    public void shouldGetUserById() {
        User user = userStorage.getUserById(1).orElse(null);

        assertNotNull(user);
        assertEquals(user.getName(), "name");
    }

    @Test
    public void shouldAddFriend() {
        User friend = User.builder()
                .name("name")
                .login("login")
                .email("email@email.ru")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();
        User user = userStorage.getUserById(1).orElseThrow();
        userStorage.createUser(friend);
        User friend1 = userStorage.getUserById(2).orElseThrow();
        userStorage.addFriend(user, friend1);

        assertEquals(userStorage.getFriends(friend1).size(), 0);
        assertEquals(userStorage.getFriends(user).size(), 1);
    }

    @Test
    public void shouldRemoveFriend() {
        User friend = User.builder()
                .name("name")
                .login("login")
                .email("email@email.ru")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();
        User user = userStorage.getUserById(1).orElseThrow();
        userStorage.createUser(friend);
        User friend1 = userStorage.getUserById(2).orElseThrow();
        userStorage.addFriend(user, friend1);
        userStorage.removeFriend(user, friend1);

        assertEquals(userStorage.getFriends(friend1).size(), 0);
        assertEquals(userStorage.getFriends(user).size(), 0);
    }
}
