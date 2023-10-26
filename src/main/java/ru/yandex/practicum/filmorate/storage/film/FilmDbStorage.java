package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private Map<String, Object> getParams(Film film) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("duration", film.getDuration());
        parameters.put("likes", film.getLikes());
        parameters.put("mpa_id", film.getMpa() != null ? film.getMpa().getId() : null);
        parameters.put("film_id", film.getId());
        return parameters;
    }


    private void saveFilmGenres(Film film) {
        String sql = "DELETE " +
                "FROM film_genre " +
                "WHERE film_id= :film_id; ";
        namedParameterJdbcTemplate.update(sql, getParams(film));
        String sql2 = "MERGE " +
                "INTO film_genre AS f " +
                "USING VALUES (?, ?) AS val(film_id, genre_id) " +
                "ON f.film_id = val.film_id " +
                "AND f.genre_id = val.genre_id " +
                "WHEN NOT MATCHED THEN " +
                "INSERT " +
                "VALUES (val.film_id, val.genre_id)" +
                "WHEN MATCHED " +
                "THEN " +
                "UPDATE " +
                "SET f.film_id = val.film_id, " +
                "f.genre_id = val.genre_id";
        namedParameterJdbcTemplate.getJdbcTemplate().batchUpdate(sql2, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre1 = film.getGenres().get(i);
                ps.setInt(1, film.getId());
                ps.setInt(2, genre1.getId());
            }

            @Override
            public int getBatchSize() {
                return film.getGenres().size();
            }
        });
    }


    private void saveFilmDirectors(Film film) {
        String sql = "DELETE " +
                "FROM films_directors " +
                "WHERE film_id= :film_id; ";
        namedParameterJdbcTemplate.update(sql, getParams(film));
        String sql2 = "MERGE " +
                "INTO films_directors AS f " +
                "USING VALUES (?, ?) AS val(film_id, dir_id) " +
                "ON f.film_id = val.film_id " +
                "AND f.director_id = val.dir_id " +
                "WHEN NOT MATCHED THEN " +
                "INSERT " +
                "VALUES (val.film_id, val.dir_id)" +
                "WHEN MATCHED " +
                "THEN " +
                "UPDATE " +
                "SET f.film_id = val.film_id, " +
                "f.director_id = val.dir_id";
        namedParameterJdbcTemplate.getJdbcTemplate().batchUpdate(sql2, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Director director = film.getDirectors().get(i);
                ps.setInt(1, film.getId());
                ps.setInt(2, director.getId());
            }

            @Override
            public int getBatchSize() {
                return film.getDirectors().size();
            }
        });
    }


    private List<Film> getAllFilms() {
        String sql = "SELECT *" +
                "FROM films AS f " +
                "LEFT JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id";

        return createFilmList(sql);
    }


    public void loadFilmGenres(List<Film> films) {
        if (films.isEmpty()) return;
        Map<Integer, Film> map = films
                .stream()
                .peek(film -> film.setGenres(new ArrayList<>()))
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        String sql = "SELECT f.film_id, " +
                "g.genre_id, " +
                "g.genre_name " +
                "FROM film_genre AS f " +
                "JOIN genres AS g ON f.genre_id = g.genre_id " +
                "WHERE f.film_id IN (:ids)";

        Map<String, Object> mapper = new HashMap<>();
        mapper.put("ids", map.keySet());

        namedParameterJdbcTemplate.query(sql, mapper, rs -> {
            while (rs.next()) {
                Film film = map.get(rs.getInt("film_id"));
                List<Genre> genres = film.getGenres();
                genres.add(new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
                film.setGenres(genres);
            }
            return map;
        });
    }

    public void loadFilmDirector(List<Film> films) {
        if (films.isEmpty()) return;
        Map<Integer, Film> map = films
                .stream()
                .peek(film -> film.setDirectors(new ArrayList<>()))
                .collect(Collectors.toMap(Film::getId, Function.identity()));

        String sql = "SELECT fd.film_id, " +
                "d.director_id, " +
                "d.director_name " +
                "FROM films_directors AS fd " +
                "JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE fd.film_id IN (:ids)";

        Map<String, Object> mapper = new HashMap<>();
        mapper.put("ids", map.keySet());

        namedParameterJdbcTemplate.query(sql, mapper, rs -> {
            while (rs.next()) {
                Film film = map.get(rs.getInt("film_id"));
                List<Director> directors = film.getDirectors();
                directors.add(new Director(rs.getInt("director_id"), rs.getString("director_name")));
                film.setDirectors(directors);
            }
            return map;
        });
    }

    private List<Film> getSortByLikes(Integer dirId) {
        try {
            String sql = "SELECT *" +
                    "FROM films AS f " +
                    "JOIN films_directors AS fd ON f.film_id = fd.film_id " +
                    "LEFT JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id " +
                    "WHERE director_id = :dirId " +
                    "ORDER BY likes DESC ";


            Map<String, Object> params = new HashMap<>();
            params.put("dirId", dirId);

            List<Film> films = createFilmList(sql, params);
            loadFilmGenres(films);
            loadFilmDirector(films);
            return films;

        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    private List<Film> getSortByYear(Integer dirId) {
        try {
            String sql = "SELECT *" +
                    "FROM films AS f " +
                    "JOIN films_directors AS fd ON f.film_id = fd.film_id " +
                    "LEFT JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id " +
                    "WHERE director_id = :dirId " +
                    "ORDER BY YEAR(release_date)";

            Map<String, Object> params = new HashMap<>();
            params.put("dirId", dirId);

            List<Film> films = createFilmList(sql, params);
            loadFilmGenres(films);
            loadFilmDirector(films);
            return films;

        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    private List<Film> getPopular(String sql, Integer limit, Integer year, Integer genreId) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("year", year);
            params.put("genreId", genreId);
            params.put("limit", limit);

            List<Film> films = createFilmList(sql, params);
            loadFilmGenres(films);
            loadFilmDirector(films);
            return films;

        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }


    @Override
    public Film createFilm(Film film) {
        String sql = "insert into films " +
                "(name, description, release_date, likes, duration, mpa_id) " +
                "values(:name, :description, :release_date, :likes, :duration, :mpa_id)";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("likes", film.getLikes())
                .addValue("duration", film.getDuration())
                .addValue("mpa_id", film.getMpa().getId());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder);
        film.setId((Integer) keyHolder.getKey());
        saveFilmGenres(film);
        saveFilmDirectors(film);
        return film;
    }


    @Override
    public void updateFilm(Film film) {
        String sql = "UPDATE films " +
                "SET name = :name, " +
                "description = :description, " +
                "release_date = :release_date, " +
                "duration = :duration," +
                "likes= :likes, " +
                "mpa_id = :mpa_id " +
                "WHERE film_id = :film_id";

        namedParameterJdbcTemplate.update(sql, getParams(film));
        saveFilmGenres(film);
        saveFilmDirectors(film);
        loadFilmGenres(List.of(film));
        loadFilmDirector(List.of(film));
    }

    @Override
    public List<Film> getSortFilm(Integer dirId, String sort) {
        switch (sort) {
            case "likes":
                return getSortByLikes(dirId);
            case "year":
                return getSortByYear(dirId);
            default:
                throw new NotFoundException("Некорректный ввод");
        }
    }


    @Override
    public List<Film> getFilms() {
        List<Film> films = getAllFilms();
        loadFilmGenres(films);
        loadFilmDirector(films);

        return films;
    }

    @Override
    public List<Film> getMostPopular(Integer limit, Integer year, Integer genreId) {
        String sql;
        if (year != null && genreId == null) {
            sql = "SELECT *" +
                    "FROM films AS f " +
                    "LEFT JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id " +
                    "WHERE YEAR(release_date) = :year " +
                    "ORDER BY likes DESC " +
                    "LIMIT :limit";
        } else if (year == null && genreId != null) {
            sql = "SELECT * " +
                    "FROM films AS f " +
                    "JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id " +
                    "WHERE fg.genre_id = :genreId " +
                    "ORDER BY likes DESC " +
                    "LIMIT :limit";
        } else if (year != null) {
            sql = "SELECT * " +
                    "FROM films AS f " +
                    "JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id " +
                    "WHERE fg.genre_id = :genreId " +
                    "AND YEAR(release_date) = :year " +
                    "ORDER BY likes DESC " +
                    "LIMIT :limit";
        } else {
            sql = "SELECT * " +
                    "FROM films AS f " +
                    "LEFT JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id " +
                    "ORDER BY likes DESC " +
                    "LIMIT :limit";
        }
        return getPopular(sql, limit, year, genreId);
    }


    @Override
    public Optional<Film> getFilmById(int id) {
        try {
            String sql = "SELECT * " +
                    "FROM films AS f " +
                    "LEFT JOIN mpa_ratings AS mr ON f.mpa_id = mr.mpa_id " +
                    "WHERE f.film_id = :film_id";
            Map<String, Object> mapper = new HashMap<>();
            mapper.put("film_id", id);

            Optional<Film> film = namedParameterJdbcTemplate.queryForObject(sql, mapper,
                    (rs, rowNum) -> Optional.of(new Film(
                            rs.getInt("film_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getInt("duration"),
                            rs.getInt("likes"),
                            new RateMPA(rs.getInt("mpa_id"), rs.getString("mpa_name")),
                            new ArrayList<>(),
                            new ArrayList<>()
                    )));
            loadFilmGenres(List.of(film.orElseThrow()));
            loadFilmDirector(List.of(film.orElseThrow()));
            return film;

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Override
    public void addLike(User user, Film film) {
        String sql = "MERGE " +
                "INTO likes AS l " +
                "USING VALUES (:film_id, :user_id) AS val(film_id, user_id) " +
                "ON l.film_id = val.film_id " +
                "AND l.user_id = val.user_id " +
                "WHEN NOT MATCHED THEN " +
                "INSERT " +
                "VALUES (val.film_id, val.user_id);" +
                "UPDATE films " +
                "SET likes = (SELECT COUNT(user_id) " +
                "FROM likes " +
                "WHERE film_id= :film_id) " +
                "WHERE film_id = :film_id";

        Map<String, Object> params = new HashMap<>();
        params.put("film_id", film.getId());
        params.put("user_id", user.getId());
        namedParameterJdbcTemplate.update(sql, params);
    }


    @Override
    public void removeLike(User user, Film film) {
        String sql = "DELETE " +
                "FROM likes " +
                "WHERE film_id = :film_id " +
                "AND user_id = :user_id;" +
                "UPDATE films " +
                "SET likes = (SELECT COUNT(user_id) " +
                "FROM likes " +
                "WHERE film_id= :film_id) " +
                "WHERE film_id = :film_id";

        Map<String, Object> params = new HashMap<>();
        params.put("film_id", film.getId());
        params.put("user_id", user.getId());

        namedParameterJdbcTemplate.update(sql, params);
    }


    @Override
    public List<Film> getUserFilms(User user) {
        try {
            String sql = "SELECT * " +
                    "FROM films AS t " +
                    "LEFT JOIN mpa_ratings AS mr ON t.mpa_id = mr.mpa_id " +
                    "JOIN likes AS l ON t.film_id = l.film_id " +
                    "JOIN users AS u ON l.user_id = u.user_id " +
                    "WHERE u.user_id = :user_id";

            Map<String, Object> params = new HashMap<>();
            params.put("user_id", user.getId());

            List<Film> films = createFilmList(sql, params);
            loadFilmGenres(films);
            loadFilmDirector(films);
            return films;

        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteFilm(int filmId) {

        String deleteFilmSql = "DELETE FROM films WHERE film_id = :filmId";

        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);

        namedParameterJdbcTemplate.update(deleteFilmSql, params);
    }

    public List<Film> createFilmList(String sql, Map<String, Object> params) {
        return namedParameterJdbcTemplate.query(sql, params, rs -> {
            List<Film> list = new ArrayList<>();
            while (rs.next()) {
                Film film = new Film();
                film.setId(rs.getInt("film_id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));
                film.setLikes(rs.getInt("likes"));
                film.setMpa(new RateMPA(rs.getInt("mpa_id"), rs.getString("mpa_name")));
                list.add(film);
            }
            return list;
        });
    }

    public List<Film> createFilmList(String sql) {
        return namedParameterJdbcTemplate.query(sql, rs -> {
            List<Film> list = new ArrayList<>();
            while (rs.next()) {
                Film film = new Film();
                film.setId(rs.getInt("film_id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setDuration(rs.getInt("duration"));
                film.setLikes(rs.getInt("likes"));
                film.setMpa(new RateMPA(rs.getInt("mpa_id"), rs.getString("mpa_name")));
                list.add(film);
            }
            return list;
        });
    }

    @Override
    public List<Film> findCommonFilms(Integer userId, Integer friendId) {
        String sql = "SELECT f.* FROM likes AS l1 " +
                "JOIN likes AS l2 ON l1.film_id = l2.film_id " +
                "JOIN films AS f ON l1.film_id = f.film_id " +
                "JOIN mpa AS m ON f.mpa_id = m.id " +
                "WHERE l1.user_id = :userId AND l2.user_id = :friendId";

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("friendId", friendId);

        List<Film> commonFilms = namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setLikes(rs.getInt("likes"));
            film.setMpa(new RateMPA(rs.getInt("mpa_id"), rs.getString("mpa_name")));
            return film;
        });
        return commonFilms;
    }
}
