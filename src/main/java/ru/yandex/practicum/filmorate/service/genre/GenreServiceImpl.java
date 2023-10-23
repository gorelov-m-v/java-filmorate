package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genres.GenreDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreDbStorage storage;

    @Override
    public Genre getGenreById(Integer id) {
        return storage.getGenreById(id);
    }

    @Override
    public List<Genre> getAllGenres() {
        return storage.getAllGenres();
    }
}
