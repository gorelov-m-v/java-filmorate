package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Director> getAllDirectors() {
        String sql = "SELECT *" +
                "FROM directors AS f ";

        return namedParameterJdbcTemplate.query(sql, rs -> {
            List<Director> list = new ArrayList<>();
            while (rs.next()) {
                Director director = new Director();
                director.setId(rs.getInt("director_id"));
                director.setName(rs.getString("director_name"));
                list.add(director);
            }
            return list;
        });
    }

    @Override
    public Optional<Director> getDirectorById(Integer id) {
        try {
            String sql = "SELECT * " +
                    "FROM directors " +
                    "WHERE director_id = :id";
            Map<String, Object> mapper = new HashMap<>();
            mapper.put("id", id);

            return namedParameterJdbcTemplate.queryForObject(sql, mapper,
                    (rs, rowNum) -> Optional.of(new Director(
                            rs.getInt("director_id"),
                            rs.getString("director_name")
                    )));

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Director createDirector(Director director) {
        String sql = "insert into directors " +
                "(director_name) " +
                "values(:name)";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("name", director.getName());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder);
        director.setId((Integer) keyHolder.getKey());
        return director;
    }

    @Override
    public void updateDirector(Director director) {
        String sql = "UPDATE directors " +
                "SET director_name = :name " +
                "WHERE director_id = :id";
        Map<String, Object> map = new HashMap<>();
        map.put("id", director.getId());
        map.put("name", director.getName());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void deleteDirectorById(Integer id) {
        String sql = "DELETE " +
                "FROM films_directors " +
                "WHERE director_id = :id; " +
                "DELETE " +
                "FROM directors " +
                "WHERE director_id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        namedParameterJdbcTemplate.update(sql, params);
    }
}
