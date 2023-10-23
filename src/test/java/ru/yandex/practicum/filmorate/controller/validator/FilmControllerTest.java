package ru.yandex.practicum.filmorate.controller.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FilmControllerTest {
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.usingContext().getValidator();

    @Test
    @DisplayName("Если id отрицательное")
    void shouldCheckIdNotNegative() {
        final Film film = new Film(-1, "name", "desc",
                LocalDate.of(1999, 12, 12), 10, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");

        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если id положительное")
    void shouldRightId() {
        final Film film = new Film(1, "name", "desc",
                LocalDate.of(1999, 12, 12), 10, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если id = null")
    void shouldRightIdIfNull() {
        final Film film = new Film(0, "name", "desc",
                LocalDate.of(1999, 12, 12), 10, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если название пустое")
    void shouldCheckNameEmpty() {
        final Film film = new Film(1, null, "desc",
                LocalDate.of(1999, 12, 12), 10, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertFalse(validates.isEmpty());
        assertEquals(message, "название не может быть пустым");
    }

    @Test
    @DisplayName("Если название не пустое")
    void shouldCheckNameNotEmpty() {
        final Film film = new Film(1, "name", "desc",
                LocalDate.of(1999, 12, 12), 10, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если описание > 200 символов")
    void shouldCheckDescToLong() {
        final Film film = new Film(1, "name", "a".repeat(201),
                LocalDate.of(1999, 12, 12), 10, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertFalse(validates.isEmpty());
        assertEquals(message, "длина описания не может быть больше 200 символов");
    }

    @Test
    @DisplayName("Если описание = 200 символов")
    void shouldCheckDescMaxLong() {
        final Film film = new Film(1, "name", "a".repeat(200),
                LocalDate.of(1999, 12, 12), 10, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если описание < 200 символов")
    void shouldCheckDescMinLong() {
        final Film film = new Film(1, "name", "a".repeat(100),
                LocalDate.of(1999, 12, 12), 10, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если дата = 0")
    void shouldCheckDateNotNull() {
        final Film film = new Film(1, "name", "desc",
                null, 10, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertFalse(validates.isEmpty());
        assertEquals(message, "дата не может быть null");
    }

    @Test
    @DisplayName("Если дата < 1895-12-28")
    void shouldCheckDateInPast() {
        final Film film = new Film(1, "name", "desc",
                LocalDate.of(1895, 12, 27), 10, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertFalse(validates.isEmpty());
        assertEquals(message, "дата релиза — не раньше 28 декабря 1895 года");
    }

    @Test
    @DisplayName("Если дата > 1895-12-28")
    void shouldCheckDateNotInPast() {
        final Film film = new Film(1, "name", "desc",
                LocalDate.of(1895, 12, 29), 10, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если дата = 1895-12-28")
    void shouldCheckDateMinValue() {
        final Film film = new Film(1, "name", "desc",
                LocalDate.of(1895, 12, 28), 10, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если продолжительность < 0")
    void shouldCheckDurationNotNegative() {
        final Film film = new Film(1, "name", "desc",
                LocalDate.of(1895, 12, 29), -1, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertFalse(validates.isEmpty());
        assertEquals(message, "продолжительность фильма должна быть положительной");
    }

    @Test
    @DisplayName("Если продолжительность = 0")
    void shouldCheckDurationNull() {
        final Film film = new Film(1, "name", "desc",
                LocalDate.of(1895, 12, 29), 0, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertFalse(validates.isEmpty());
        assertEquals(message, "продолжительность фильма должна быть положительной");
    }

    @Test
    @DisplayName("Если продолжительность > 0")
    void shouldCheckDurationPositive() {
        final Film film = new Film(1, "name", "desc",
                LocalDate.of(1895, 12, 29), 1, 0, null, null);

        Set<ConstraintViolation<Film>> validates = validator.validate(film);
        assertEquals(0, validates.size());
    }
}
