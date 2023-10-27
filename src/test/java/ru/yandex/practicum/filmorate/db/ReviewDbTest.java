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
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final DirectorDbStorage storageDir;
    private final ReviewDbStorage storage;

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
        filmDbStorage.createFilm(film);
        userDbStorage.createUser(user);

        Review review = new Review(0,
                "Плохой фильм",
                false,
                1,
                1,
                0);

        storage.createReview(review);
    }

    @Test
    public void shouldFindReview() {
        Review review = new Review(1,
                "Плохой фильм",
                false,
                1,
                1,
                0);

        assertEquals(review, storage.getById(1));
    }

    @Test
    public void shouldUpdateReview() {
        Review reviewToUpdate = new Review(1,
                "Хороший фильм",
                true,
                1,
                1,
                0);

        assertEquals(reviewToUpdate, storage.updateReview(reviewToUpdate));
    }

    @Test
    public void shouldDeleteReview() {
        storage.deleteReview(1);

        assertThrows(NotFoundException.class, () -> storage.getById(1));
    }

}
