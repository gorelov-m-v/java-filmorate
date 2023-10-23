package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.RateMPA;

import java.util.List;

public interface MpaStorage {

    RateMPA getRateById(Integer id);

    List<RateMPA> getAllRatings();
}
