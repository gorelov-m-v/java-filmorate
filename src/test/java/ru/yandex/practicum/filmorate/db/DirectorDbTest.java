package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RateMPA;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorDbTest {
    private final DirectorStorage storage;
    private final FilmStorage storageFilm;

    @BeforeEach
    public void createParams() {
        RateMPA rateMPA = new RateMPA(1, null);
        Genre genre = new Genre(1, null);
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(1)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .mpa(rateMPA)
                .genres(List.of(genre))
                .directors(new ArrayList<>())
                .build();
        storageFilm.createFilm(film);
        storage.createDirector(new Director(1, "new Director"));
    }

    @Test
    public void testFindAllDirectors() {
        List<Director> directors = storage.getAllDirectors();

        assertEquals(1, directors.size());
    }

    @Test
    public void testFindByIdDirector() {
        Director director = storage.getDirectorById(1).orElseThrow();

        assertEquals("new Director", director.getName());
    }

    @Test
    public void testDeleteByIdDirector() {
        storage.deleteDirectorById(1);

        assertThrows(NotFoundException.class, () -> {
            storage.getDirectorById(1)
                    .orElseThrow(() -> new NotFoundException("Не найдено"));
        }, "Не найдено");
    }

    @Test
    public void testUpdateByIdDirector() {
        storage.updateDirector(new Director(1, "updated"));
        Director director = storage.getDirectorById(1).orElseThrow();

        assertEquals("updated", director.getName());
    }
}
