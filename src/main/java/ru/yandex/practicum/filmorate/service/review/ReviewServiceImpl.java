package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    ReviewStorage storage;

    @Override
    public Review createReview(Review review) {
        System.out.println(review);
        return storage.createReview(review);
    }

    @Override
    public Review updateReview(Review review) {
        return storage.updateReview(review);
    }

    @Override
    public Review getById(Integer id) {
        return storage.getById(id);
    }

    @Override
    public List<Review> getReviews(Integer filmId, Integer count) {
        return storage.getReviews(filmId, count);
    }

    @Override
    public void deleteReview(Integer id) {
        storage.deleteReview(id);
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        storage.addLike(id, userId);
    }

    @Override
    public void addDislike(Integer id, Integer userId) {
        storage.addDislike(id, userId);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        storage.deleteLike(id, userId);
    }

    @Override
    public void deleteDislike(Integer id, Integer userId) {
        storage.deleteDislike(id, userId);
    }
}
