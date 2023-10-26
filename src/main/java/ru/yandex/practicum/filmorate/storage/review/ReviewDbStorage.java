package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private Map<String, Object> getParams(Review review) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_id", review.getUserId());
        parameters.put("film_id", review.getFilmId());
        parameters.put("is_positive", review.getIsPositive());
        parameters.put("content", review.getContent());
        parameters.put("review_id", review.getReviewId());
        parameters.put("useful", review.getUseful());
        return parameters;
    }

    @Override
    public Review createReview(Review review) {
        System.out.println(review);
        String sql = "insert into reviews " +
                "(content, is_positive, film_id, user_id, useful) " +
                "values(:content, :is_positive, :film_id, :user_id, :useful)";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("is_positive", review.getIsPositive())
                .addValue("film_id", review.getFilmId())
                .addValue("user_id", review.getUserId())
                .addValue("useful", review.getUseful());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder);
        review.setReviewId((Integer) keyHolder.getKey());

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews " +
                "SET content = :content, " +
                "is_positive = :is_positive " +
                "WHERE review_id = :review_id";

        int rows = namedParameterJdbcTemplate.update(sql, getParams(review));
        if (rows == 0) {
            log.warn("отзыв с id {}, c film_id {}, c user_id {} не найден.",
                    review.getReviewId(),
                    review.getFilmId(),
                    review.getUserId());
            throw new NotFoundException(
                    String.format("отзыв с id %d, c film_id %d, c user_id %d не найден.",
                            review.getReviewId(),
                            review.getFilmId(),
                            review.getUserId()));
        }

        return getById(review.getReviewId());
    }

    @Override
    public Review getById(Integer id) {
        try {
            String sql = "SELECT * " +
                    "FROM reviews " +
                    "WHERE review_id = :review_id";
            Map<String, Object> mapper = new HashMap<>();
            mapper.put("review_id", id);
            return namedParameterJdbcTemplate.queryForObject(sql, mapper,
                    (rs, rowNum) -> new Review(
                            rs.getInt("review_id"),
                            rs.getString("content"),
                            rs.getBoolean("is_positive"),
                            rs.getInt("film_id"),
                            rs.getInt("user_id"),
                            rs.getInt("useful")

                    ));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Отзыв с id %d не найден.", id));
        }
    }

    @Override
    public List<Review> getReviews(Integer filmId, Integer count) {
        String sql;
        if (filmId != null) {
            sql = "SELECT * " +
                    "FROM reviews " +
                    "WHERE film_id = :film_id " +
                    "ORDER BY useful DESC, review_id " +
                    "LIMIT :limit";
        } else {
            sql = "SELECT * " +
                    "FROM reviews " +
                    "ORDER BY useful DESC, review_id " +
                    "LIMIT :limit";
        }
        return getReviewList(sql, filmId, count);
    }

    @Override
    public void deleteReview(Integer id) {

        String deleteReviewSql = "DELETE FROM reviews WHERE review_id = :review_id";

        Map<String, Object> params = new HashMap<>();
        params.put("review_id", id);

        namedParameterJdbcTemplate.update(deleteReviewSql, params);
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        String sql = "INSERT INTO reviews_likes (review_id, user_id, is_like) " +
                "VALUES(:review_id, :user_id, true); " +
                "UPDATE reviews AS r SET r.useful = r.useful + 1 " +
                "WHERE r.review_id = :review_id;";

        Map<String, Object> params = new HashMap<>();
        params.put("review_id", id);
        params.put("user_id", userId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void addDislike(Integer id, Integer userId) {
        String sql = "INSERT INTO reviews_likes (review_id, user_id, is_like) " +
                "VALUES(:review_id, :user_id, false); " +
                "UPDATE reviews AS r SET r.useful = r.useful - 1 " +
                "WHERE r.review_id = :review_id;";

        Map<String, Object> params = new HashMap<>();
        params.put("review_id", id);
        params.put("user_id", userId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        String sql = "DELETE FROM reviews_likes " +
                "WHERE review_id = :review_id " +
                "AND user_id = :user_id " +
                "AND is_like = true; " +
                "UPDATE reviews AS r SET r.useful = r.useful - 1 " +
                "WHERE r.review_id = :review_id;";

        Map<String, Object> params = new HashMap<>();
        params.put("review_id", id);
        params.put("user_id", userId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteDislike(Integer id, Integer userId) {
        String sql = "DELETE FROM reviews_likes " +
                "WHERE review_id = :review_id " +
                "AND user_id = :user_id " +
                "AND is_like = true; " +
                "UPDATE reviews AS r SET r.useful = r.useful + 1 " +
                "WHERE r.review_id = :review_id;";

        Map<String, Object> params = new HashMap<>();
        params.put("review_id", id);
        params.put("user_id", userId);
        namedParameterJdbcTemplate.update(sql, params);
    }

    private List<Review> getReviewList(String sql, Integer filmId, Integer count) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("film_id", filmId);
            params.put("limit", count);

            return createReviewList(sql, params);

        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    public List<Review> createReviewList(String sql, Map<String, Object> params) {
        return namedParameterJdbcTemplate.query(sql, params, rs -> {
            List<Review> list = new ArrayList<>();
            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setContent(rs.getString("content"));
                review.setIsPositive(rs.getBoolean("is_positive"));
                review.setFilmId(rs.getInt("film_id"));
                review.setUserId(rs.getInt("user_id"));
                review.setUseful(rs.getInt("useful"));
                list.add(review);
            }
            return list;
        });
    }
}
