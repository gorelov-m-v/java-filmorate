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
    private final DirectorStorage directorStorage;

    private Director getStorageDirectorId(Integer id) {
        return directorStorage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с id " + id + " не найден."));
    }

    @Override
    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    @Override
    public Director getDirectorById(Integer id) {
        return getStorageDirectorId(id);
    }

    @Override
    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        getStorageDirectorId(director.getId());
        directorStorage.updateDirector(director);
        return director;
    }

    @Override
    public void deleteDirectorById(Integer id) {
        getStorageDirectorId(id);
        directorStorage.deleteDirectorById(id);
    }
}
