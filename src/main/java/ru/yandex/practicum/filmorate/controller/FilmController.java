package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SearchFilmRequest;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление нового фильма");
        Film film1 = filmService.createFilm(film);
        log.info("Добавлен новый фильм");
        return film1;
    }

    @GetMapping("/director/{id}")
    public List<Film> getSortFilms(@PathVariable Integer id,
                                   @RequestParam String sortBy) {
        return filmService.getSortFilms(id, sortBy);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление фильма");
        if (film.getId() == null) throw new ValidationException("Значение id не может равняться null");
        Film film1 = filmService.updateFilm(film);
        log.info("Фильм обновлен");
        return film1;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Запрос на получение списка всех фильмов");
        List<Film> films = filmService.getFilms();
        log.info("Список всех фильмов отправлен");
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        log.info("Запрос на получение фильма с id - " + id);
        Film film = filmService.getById(id);
        log.info("Фильм с id - " + id + " отправлен");
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Запрос на добавление лайка фильму - " + id);
        filmService.addLike(userId, id);
        log.info("Пользователь с id - " + userId + " поставил лайк фильму - " + id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Запрос на удаление лайка фильму - " + id);
        filmService.removeLike(id, userId);
        log.info("Пользователь с id - " + userId + " удалил лайк фильму - " + id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") Integer count,
                                 @RequestParam(required = false) Integer year,
                                 @RequestParam(required = false) Integer genreId) {
        log.info("Запрос на получение списка популярных фильмов");
        List<Film> films = filmService.getPopular(count, year, genreId);
        log.info("Список популярных фильмов отправлен");
        return films;
    }

    @GetMapping("/common")
    public ResponseEntity<List<Film>> getCommonFilms(
            @RequestParam Integer userId,
            @RequestParam Integer friendId
    ) {
        List<Film> commonFilms = filmService.findCommonFilms(userId, friendId);
        log.info("Запрос на получение общих фильмов для пользователей с userId - " + userId + " и friendId - " + friendId);
        return ResponseEntity.ok(commonFilms);
    }

    @DeleteMapping("/{filmId}")
    public ResponseEntity<?> deleteFilm(@PathVariable Integer filmId) {
        log.info("Запрос на удаление фильма с id - " + filmId);
        filmService.deleteFilm(filmId);
        log.info("Фильм с id - " + filmId + " удален");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public List<Film> searchFilm(@RequestParam String query, @RequestParam String by) {
        SearchFilmRequest request = new SearchFilmRequest();
        request.setQuery(query);
        request.setBy(by);
        List<Film> films = filmService.searchFilm(request);
        log.info("Фильм успешно найден");
        return films;

    }
}
