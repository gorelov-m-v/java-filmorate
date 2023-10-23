package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.RateMPA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public RateMPA getRateById(Integer id) {
        try {
            String sql = "SELECT * " +
                    "FROM mpa_ratings  " +
                    "WHERE mpa_id = :mpa_id";

            Map<String, Object> mapper = new HashMap<>();
            mapper.put("mpa_id", id);
            return namedParameterJdbcTemplate.queryForObject(sql, mapper,
                    (rs, rowNum) -> new RateMPA(rs.getInt("mpa_id"),
                            rs.getString("mpa_name")));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Рейтинг с id %d не найден.", id));
        }
    }


    @Override
    public List<RateMPA> getAllRatings() {
        String sql = "SELECT * " +
                "FROM mpa_ratings";
        return namedParameterJdbcTemplate.getJdbcTemplate()
                .query(sql, (rs, rowNum) -> new RateMPA(rs.getInt("mpa_id"),
                        rs.getString("mpa_name")));
    }
}
