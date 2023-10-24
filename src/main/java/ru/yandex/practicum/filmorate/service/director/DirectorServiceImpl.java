package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage storage;


    private Director getStorageDirectorId(Integer id) {
        return storage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id " + id + " не найден."));
    }

    @Override
    public List<Director> getAllDirectors() {
        return storage.getAllDirectors();
    }

    @Override
    public Director getDirectorById(Integer id) {
        return getStorageDirectorId(id);
    }

    @Override
    public Director createDirector(Director director) {
        return storage.createDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        getStorageDirectorId(director.getId());
        storage.updateDirector(director);
        return director;
    }

    @Override
    public void deleteDirectorById(Integer id) {
        getStorageDirectorId(id);
        storage.deleteDirectorById(id);
    }
}
