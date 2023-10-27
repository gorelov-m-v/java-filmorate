package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(Review review);

    Review updateReview(Review review);

    Review getById(Integer id);

    List<Review> getReviews(Integer filmId, Integer count);

    void deleteReview(Integer id);

    void addLike(Integer id, Integer userId);

    void addDislike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);

    void deleteDislike(Integer id, Integer userId);

}
