package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RateMPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class  FilmDbTest {
    private final FilmDbStorage storage;
    private final UserDbStorage userStorage;


    @BeforeEach
    public void createParams() {
        RateMPA rateMPA = new RateMPA(1, null);
        Genre genre = new Genre(1, null);
        User user = User.builder()
                .name("name")
                .login("login")
                .email("email@email.ru")
                .birthday(LocalDate.of(2000, 12, 12))
                .build();
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(1)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .mpa(rateMPA)
                .genres(List.of(genre))
                .build();
        storage.createFilm(film);
        userStorage.createUser(user);
    }


    @Test
    public void testFindFilmById() {
        Optional<Film> userOptional = storage.getFilmById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }


    @Test
    public void testFindAllFilm() {
        List<Film> films = storage.getFilms();

        assertEquals(1, films.size());
    }


    @Test
    public void testFindGenreByFilm() {
        Film film = storage.getFilmById(1).orElseThrow();
        List<Genre> genre = film.getGenres();

        assertEquals(1, genre.size());
        assertEquals(genre.get(0).getName(), "Комедия");
    }


    @Test
    public void testUpdateFilm() {
        Film film = new Film(1, "new", "new",
                LocalDate.of(1999, 1, 1), 1, 0,
                new RateMPA(3, "PG-13"), List.of(new Genre(3, "Мультфильм")));
        storage.updateFilm(film);
        Film film1 = storage.getFilmById(1).orElseThrow();

        assertThat(film1.getName())
                .isEqualTo(film.getName());
        assertThat(film1.getDescription())
                .isEqualTo(film.getDescription());
        assertThat(film1.getDuration())
                .isEqualTo(film.getDuration());
        assertThat(film1.getReleaseDate())
                .isEqualTo(film.getReleaseDate());
        assertThat(film1.getLikes())
                .isEqualTo(film.getLikes());
        assertThat(film1.getGenres().get(0).getName())
                .isEqualTo(film.getGenres().get(0).getName());
        assertThat(film1.getMpa().getName())
                .isEqualTo(film.getMpa().getName());
    }


    @Test
    public void testAddLikeToFilm() {
        Film film = storage.getFilmById(1).orElseThrow();
        User user = userStorage.getUserById(1).orElseThrow();
        storage.addLike(user, film);

        assertEquals(1, storage.getFilmById(1).orElseThrow().getLikes());
    }

    @Test
    public void testRemoveLikeToFilm() {
        Film film = storage.getFilmById(1).orElseThrow();
        User user = userStorage.getUserById(1).orElseThrow();
        storage.addLike(user, film);
        storage.removeLike(user, film);

        assertEquals(0, storage.getFilmById(1).orElseThrow().getLikes());
    }


    @Test
    public void testGetUserFilms() {
        Film film = storage.getFilmById(1).orElseThrow();
        User user = userStorage.getUserById(1).orElseThrow();
        storage.addLike(user, film);
        List<Film> films = storage.getUserFilms(userStorage.getUserById(1).orElseThrow());

        assertEquals(1, films.size());
    }

    @Test
    public void testFindCommonFilms() {
        User user1 = userStorage.getUserById(1).orElseThrow();
        User user2 = userStorage.createUser(User.builder()
                .name("friend")
                .login("friend_login")
                .email("friend@example.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build());

        Film film1 = storage.getFilmById(1).orElseThrow();
        Film film2 = Film.builder()
                .name("film2")
                .description("desc2")
                .duration(2)
                .releaseDate(LocalDate.of(2001, 1, 1))
                .mpa(new RateMPA(2, "PG"))
                .genres(List.of(new Genre(2, "Драма")))
                .build();
        storage.createFilm(film2);

        storage.addLike(user1, film1);
        storage.addLike(user1, film2);
        storage.addLike(user2, film1);
        storage.addLike(user2, film2);

        List<Film> commonFilms = storage.findCommonFilms(user1.getId(), user2.getId());
        assertEquals(2, commonFilms.size());
        assertTrue(commonFilms.contains(film1));
        assertTrue(commonFilms.contains(film2));
    }

}
