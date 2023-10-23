package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.RateMPA;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaDbStorage storage;


    @Override
    public List<RateMPA> getAllRatings() {
        return storage.getAllRatings();
    }


    @Override
    public RateMPA getMpaById(Integer id) {
        return storage.getRateById(id);
    }
}
