package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;
    private final DirectorStorage storageDir;

    private Film getStorageFilmId(Integer id) {
        return storage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }


    @Override
    public Film createFilm(Film film) {
        return storage.createFilm(film);
    }


    @Override
    public Film updateFilm(Film film) {
        getStorageFilmId(film.getId());
        storage.updateFilm(film);
        return film;
    }


    @Override
    public Film getById(Integer id) {
        return getStorageFilmId(id);
    }


    @Override
    public List<Film> getFilms() {
        return storage.getFilms();
    }


    @Override
    public List<Film> getSortFilms(Integer dirId, String sort) {
        storageDir.getDirectorById(dirId)
                .orElseThrow(() -> new NotFoundException("Режиссер с id - " + dirId + " не найден"));
        return storage.getSortFilm(dirId, sort);
    }


    @Override
    public void addLike(Integer userId, Integer filmId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Film film = getStorageFilmId(filmId);
        storage.addLike(user, film);
    }


    @Override
    public void removeLike(Integer filmId, Integer userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c id - " + userId + " не найден"));
        Film film = getStorageFilmId(filmId);
        storage.removeLike(user, film);
    }


    @Override
    public List<Film> getPopular(Integer limit, Integer year, Integer genreId) {
        return storage.getMostPopular(limit, year, genreId);
    }

    @Override
    public void deleteFilm(Integer filmId) {
        Film film = getStorageFilmId(filmId);
        storage.deleteFilm(filmId);
    }

    public List<Film> searchFilm(String query, String by) {
        if (query == null || by == null) {
            throw new IllegalArgumentException("Неверные параметры запроса");
        }

        query = query.toLowerCase();

        if (by.equalsIgnoreCase("title,director")) {
            List<Film> filmsByParams = storage.getFilmsByParams(query);

            Set<Film> result = new HashSet<>(filmsByParams);


            return new ArrayList<>(result);
        } else if (by.equalsIgnoreCase("director")) {
            return storage.getFilmByDirector(query);
        } else if (by.equalsIgnoreCase("title")) {
            return storage.getFilmByName(query);
        } else {
            throw new IllegalArgumentException("Неверное значение параметра 'by'");
        }
    }











}
