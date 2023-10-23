package ru.yandex.practicum.filmorate.storage.genres;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * " +
                "FROM genres";
        return namedParameterJdbcTemplate.getJdbcTemplate()
                .query(sql, (rs, rowNum) -> new Genre(rs.getInt("genre_id"),
                        rs.getString("genre_name")));
    }

    @Override
    public Genre getGenreById(Integer id) {
        try {
            String sql = "SELECT * " +
                    "FROM genres  " +
                    "WHERE genre_id = :genre_id";

            Map<String, Object> mapper = new HashMap<>();
            mapper.put("genre_id", id);
            return namedParameterJdbcTemplate.queryForObject(sql, mapper,
                    (rs, rowNum) -> new Genre(rs.getInt("genre_id"),
                            rs.getString("genre_name")));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Жанр с id %d не найден.", id));
        }
    }
}
