package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;


public interface DirectorStorage {
    List<Director> getAllDirectors();

    Optional<Director> getDirectorById(Integer id);

    Director createDirector(Director director);

    void updateDirector(Director director);

    void deleteDirectorById(Integer id);
}
