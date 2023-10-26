package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final DirectorStorage storageDir;

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

    @Test
    public void testGetRecommendedFilm() {
        RateMPA rateMPA = new RateMPA(1, null);
        Genre genre = new Genre(1, null);
        storageDir.createDirector(new Director(1, "new"));
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(1)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .mpa(rateMPA)
                .genres(List.of(genre))
                .directors(List.of(storageDir.getDirectorById(1).orElseThrow()))
                .build();
        filmDbStorage.createFilm(film);
        filmDbStorage.addLike(userStorage.getUserById(1).orElseThrow(), filmDbStorage.getFilmById(1).orElseThrow());
        User user = User.builder()
                .name("name")
                .login("login")
                .email("email@email.ru")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();
        userStorage.createUser(user);
        List<Film> films = userStorage.getUserRecommendations(2);

        assertEquals(films.get(0).getId(), film.getId());
    }

    public void shouldDeleteUser() {
        User user = userStorage.getUserById(1).orElseThrow();
        userStorage.deleteUser(user);

        // Убеждаемся, что пользователь удален
        assertThrows(NotFoundException.class, () -> userStorage.getUserById(1).orElseThrow());
    }
}
