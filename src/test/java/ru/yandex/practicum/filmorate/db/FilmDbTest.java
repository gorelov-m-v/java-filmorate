package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class  FilmDbTest {
    private final FilmDbStorage storage;
    private final UserDbStorage userStorage;
    private final DirectorStorage storageDir;


    @BeforeEach
    public void createParams() {
        RateMPA rateMPA = new RateMPA(1, null);
        Genre genre = new Genre(1, null);
        storageDir.createDirector(new Director(1, "new"));
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
                .directors(List.of(storageDir.getDirectorById(1).orElseThrow()))
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
    public void testFindSortYearFilm() {
        Director director = storageDir.getDirectorById(1).orElseThrow();
        storage.createFilm(new Film(1, "new", "new",
                LocalDate.of(1999, 1, 1), 1, 0,
                new RateMPA(3, "PG-13"),
                List.of(new Genre(3, "Мультфильм")), List.of(director)));
        List<Film> films = storage.getSortFilm(1, "year");

        assertEquals(2, films.size());
        assertEquals("new", films.get(0).getName());
        assertEquals("name", films.get(1).getName());
    }

    @Test
    public void testFindSortLikeFilm() {
        Director director = storageDir.getDirectorById(1).orElseThrow();
        storage.createFilm(new Film(1, "new", "new",
                LocalDate.of(1999, 1, 1), 1, 0,
                new RateMPA(3, "PG-13"),
                List.of(new Genre(3, "Мультфильм")), List.of(director)));
        storage.addLike(userStorage.getUserById(1).orElseThrow(), storage.getFilmById(1).orElseThrow());
        List<Film> films = storage.getSortFilm(1, "likes");

        assertEquals(2, films.size());
        assertEquals("name", films.get(0).getName());
        assertEquals("new", films.get(1).getName());
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
                new RateMPA(3, "PG-13"), List.of(new Genre(3, "Мультфильм")), new ArrayList<>());
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
    public void testGetMostPopularFilmsByYear() {
        Film film = new Film(1, "new", "new",
                LocalDate.of(1999, 1, 1), 1, 0,
                new RateMPA(3, "PG-13"), List.of(new Genre(3, "Мультфильм")), new ArrayList<>());
        storage.createFilm(film);

        List<Film> films = storage.getMostPopular(1, 2000, null);
        assertEquals(1, films.size());
        assertEquals(2000, films.get(0).getReleaseDate().getYear());
    }

    @Test
    public void testGetMostPopularFilmsByGenre() {
        Film film = new Film(1, "new", "new",
                LocalDate.of(1999, 1, 1), 1, 0,
                new RateMPA(3, "PG-13"), List.of(new Genre(3, "Мультфильм")), new ArrayList<>());
        storage.createFilm(film);

        List<Film> films = storage.getMostPopular(1, null, 3);
        assertEquals(1, films.size());
        assertEquals(1999, films.get(0).getReleaseDate().getYear());
        assertEquals(3, films.get(0).getGenres().get(0).getId());
    }

    @Test
    public void testGetMostPopularFilmsByGenreAndYear() {
        Film film = new Film(1, "new", "new",
                LocalDate.of(1999, 1, 1), 1, 0,
                new RateMPA(3, "PG-13"), List.of(new Genre(3, "Мультфильм")), new ArrayList<>());
        storage.createFilm(film);

        List<Film> films = storage.getMostPopular(1, 1999, 3);
        assertEquals(1, films.size());
        assertEquals(1999, films.get(0).getReleaseDate().getYear());
        assertEquals(3, films.get(0).getGenres().get(0).getId());
    }

    @Test
    public void testGetMostPopularFilms() {
        Film film = new Film(1, "new", "new",
                LocalDate.of(1999, 1, 1), 1, 0,
                new RateMPA(3, "PG-13"), List.of(new Genre(3, "Мультфильм")), new ArrayList<>());
        storage.createFilm(film);

        List<Film> films = storage.getMostPopular(1, null, null);
        assertEquals(1, films.size());
    }

    @Test
    public void shouldDeleteFilm() {
        int filmId = 1;
        storage.deleteFilm(filmId);

        Optional<Film> deletedFilm = storage.getFilmById(filmId);
        assertFalse(deletedFilm.isPresent());
    }
}
