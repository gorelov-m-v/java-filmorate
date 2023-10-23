package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;

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
    public List<Film> getPopular(Integer limit) {
        return storage.getFilms().stream()
                .sorted((s, s1) -> Integer.compare(s1.getLikes(), s.getLikes()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
