package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        log.info("Запрос на добавление нового отзыва: " + review);
        Review reviewNew = reviewService.createReview(review);
        log.info("Добавлен новый фильм");
        return reviewNew;
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Запрос на обновление отзыва: " + review);
        if (review.getReviewId() == null) throw new ValidationException("Значение id не может равняться null");
        Review reviewNew = reviewService.updateReview(review);
        log.info("отзыв обновлен");
        return reviewNew;
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable Integer id) {
        log.info("Запрос на получение отзыва с id - " + id);
        Review review = reviewService.getById(id);
        log.info("Отзыв с id - " + id + " отправлен");
        return review;
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(name = "filmId", required = false) Integer filmId,
                                   @RequestParam(name = "count", defaultValue = "10") Integer count) {
        log.info("Запрос на получение отзывов с параметрами запроса filmId = " + filmId +
                ", count = " + count);
        List<Review> reviews = reviewService.getReviews(filmId, count);
        log.info("Отзывы отправлены");
        return reviews;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Integer id) {
        log.info("Запрос на удаление отзыва с id - " + id);
        reviewService.deleteReview(id);
        log.info("Отзыв удален");
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Запрос на добавление лайка на отзыв с id - " + id
                + ", от пользователя с id - " + userId);
        reviewService.addLike(id, userId);
        log.info("Лайк добавлен");
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Запрос на добавление дизлайка на отзыв с id - " + id
                + ", от пользователя с id - " + userId);
        reviewService.addDislike(id, userId);
        log.info("Лайк добавлен");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Запрос на удаление лайка на отзыв с id - " + id
                + ", от пользователя с id - " + userId);
        reviewService.deleteLike(id, userId);
        log.info("Лайк добавлен");
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("Запрос на добавление дизлайка на отзыв с id - " + id
                + ", от пользователя с id - " + userId);
        reviewService.deleteDislike(id, userId);
        log.info("Лайк добавлен");
    }
}
