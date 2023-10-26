package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SearchFilmRequest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public List<Film> searchFilm(@Valid SearchFilmRequest request) {
        String query = request.getQuery().toLowerCase();
        String by = request.getBy();

        if (by.equalsIgnoreCase("title,director") || by.equalsIgnoreCase("director,title")) {
            List<Film> filmsByParams = storage.getFilmsByParams(query);
            Set<Film> result = new HashSet<>(filmsByParams);
            return new ArrayList<>(result);
        } else if (by.equalsIgnoreCase("director")) {
            return storage.getFilmByDirector(query);
        } else {
            return storage.getFilmByName(query);
        }
    }

    @Override
    public List<Film> findCommonFilms(Integer userId, Integer friendId) {
        return storage.findCommonFilms(userId, friendId);
    }
}