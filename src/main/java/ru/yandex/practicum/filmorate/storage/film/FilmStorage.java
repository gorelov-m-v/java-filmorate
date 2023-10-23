package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film createFilm(Film film);

    void updateFilm(Film film);

    List<Film> getFilms();

    Optional<Film> getFilmById(int id);

    void addLike(User user, Film film);

    void removeLike(User user, Film film);

    List<Film> getUserFilms(User user);
}
