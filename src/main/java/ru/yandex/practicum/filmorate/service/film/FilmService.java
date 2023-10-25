package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getById(Integer id);

    List<Film> getFilms();

    List<Film> getSortFilms(Integer dirId, String sort);

    void addLike(Integer userId, Integer filmId);

    void removeLike(Integer filmId, Integer userId);

    List<Film> getPopular(Integer limit, Integer year, Integer genreId);

    void deleteFilm(Integer filmId);
}