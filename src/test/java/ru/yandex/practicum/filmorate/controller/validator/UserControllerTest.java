package ru.yandex.practicum.filmorate.controller.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerTest {

    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.usingContext().getValidator();

    @Test
    @DisplayName("Если id < 0")
    void shouldThrowIfIdNegative() {
        final User user1 = new User(-1, "email@email.ru", "login", "name",
                LocalDate.of(1999, 12, 12));

        Set<ConstraintViolation<User>> validates = validator.validate(user1);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если id > 0")
    void shouldRightIfIdNotNegative() {
        final User user = new User(1, "email@email.ru", "login", "name",
                LocalDate.of(1999, 12, 12));

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если id = 0")
    void shouldRightIfIdNull() {
        final User user = new User(0, "email@email.ru", "login", "name",
                LocalDate.of(1999, 12, 12));

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если email пустой")
    void shouldThrowIfEmailEmpty() {
        final User user = new User(1, "", "login", "name",
                LocalDate.of(1999, 12, 12));

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertTrue(validates.size() > 0);
        assertEquals(message, "email не может быть пустым или равняться нулю");
    }

    @Test
    @DisplayName("Если email = null")
    void shouldThrowIfEmailNull() {
        final User user = new User(1, null, "login", "name",
                LocalDate.of(1999, 12, 12));

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertTrue(validates.size() > 0);
        assertEquals(message, "email не может быть пустым или равняться нулю");
    }

    @Test
    @DisplayName("Если email не валидный")
    void shouldThrowIfEmailInvalid() {
        final User user = new User(1, "email email.ru", "login", "name",
                LocalDate.of(1999, 12, 12));

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertTrue(validates.size() > 0);
        assertEquals(message, "email не валидный");
    }

    @Test
    @DisplayName("Если email валидный")
    void shouldIfEmailValid() {
        final User user = new User(1, "email@email.ru", "login", "name",
                LocalDate.of(1999, 12, 12));

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если login пустой")
    void shouldThrowIfLoginEmpty() {
        final User user = new User(1, "email@email.ru", null, "name",
                LocalDate.of(1999, 12, 12));

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertTrue(validates.size() > 0);
        assertEquals(message, "login не может быть пустым");
    }

    @Test
    @DisplayName("Если login содержит пробелы")
    void shouldThrowIfLoginContainsSpace() {
        final User user = new User(1, "email@email.ru", "log in", "name",
                LocalDate.of(1999, 12, 12));

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertTrue(validates.size() > 0);
        assertEquals(message, "login не может содержать пробелы");
    }

    @Test
    @DisplayName("Если login валидный")
    void shouldNotThrowIfLoginIsValid() {
        final User user = new User(1, "email@email.ru", "login", "name",
                LocalDate.of(1999, 12, 12));

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если name пустое")
    void shouldNotThrowIfNameIsEmpty() {
        final User user = new User(1, "email@email.ru", "login", "",
                LocalDate.of(1999, 12, 12));

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        assertEquals(0, validates.size());
    }

    @Test
    @DisplayName("Если birthday = null")
    void shouldThrowIfBirthdayNull() {
        final User user = new User(1, "email@email.ru", "login", "name",
                null);

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertTrue(validates.size() > 0);
        assertEquals(message, "birthday не может равняться нулю");
    }

    @Test
    @DisplayName("Если birthday в будущем")
    void shouldThrowIfBirthdayInTheFuture() {
        final User user = new User(1, "email@email.ru", "login", "name",
                LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        String message = validates.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("");
        assertTrue(validates.size() > 0);
        assertEquals(message, "birthday не может быть в будущем");
    }

    @Test
    @DisplayName("Если birthday валидный")
    void shouldNotThrowIfBirthdayIsValid() {
        final User user = new User(1, "email@email.ru", "login", "name",
                LocalDate.of(1999, 12, 12));

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        assertEquals(0, validates.size());
    }
}
