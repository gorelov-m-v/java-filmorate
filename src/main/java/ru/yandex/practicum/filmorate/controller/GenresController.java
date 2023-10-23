package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenresController {
    private final GenreService service;

    @GetMapping
    public List<Genre> getGenres() {
        log.info("Запрос на получение списка всех жанров");
        List<Genre> genres = service.getAllGenres();
        log.info("Список жанров отправлен");
        return genres;
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable("id") Integer id) {
        log.info("Запрос на получение жанра по id");
        Genre genre = service.getGenreById(id);
        log.info("Жанр отправлен");
        return genre;
    }
}
