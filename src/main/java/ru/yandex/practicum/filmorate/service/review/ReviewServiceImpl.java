package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final FeedStorage feedStorage;

    @Override
    public Review createReview(Review review) {
        review = reviewStorage.createReview(review);
        feedStorage.addEvent(review.getUserId(), "REVIEW", "ADD", review.getReviewId());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        review = reviewStorage.updateReview(review);
        feedStorage.addEvent(review.getUserId(), "REVIEW", "UPDATE", review.getReviewId());
        return review;
    }

    @Override
    public Review getById(Integer id) {
        return reviewStorage.getById(id);
    }

    @Override
    public List<Review> getReviews(Integer filmId, Integer count) {
        return reviewStorage.getReviews(filmId, count);
    }

    @Override
    public void deleteReview(Integer id) {
        Review review = getById(id);
        reviewStorage.deleteReview(id);
        feedStorage.addEvent(review.getUserId(), "REVIEW", "REMOVE", review.getReviewId());
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        reviewStorage.addLike(id, userId);
    }

    @Override
    public void addDislike(Integer id, Integer userId) {
        reviewStorage.addDislike(id, userId);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        reviewStorage.deleteLike(id, userId);
    }

    @Override
    public void deleteDislike(Integer id, Integer userId) {
        reviewStorage.deleteDislike(id, userId);
    }
}
