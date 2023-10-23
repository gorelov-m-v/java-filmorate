package ru.yandex.practicum.filmorate.service.mpa;

import ru.yandex.practicum.filmorate.model.RateMPA;

import java.util.List;

public interface MpaService {
    List<RateMPA> getAllRatings();

    RateMPA getMpaById(Integer id);
}
