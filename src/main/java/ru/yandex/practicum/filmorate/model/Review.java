package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Slf4j
@ToString
@EqualsAndHashCode
public class Review {
    private Integer reviewId;
    @NotEmpty(message = "текст отзыва не может быть пустым")
    private String content;
    @NotNull(message = "значение isPositive у отзыва не может быть null")
    private Boolean isPositive;
    @NotNull
    private Integer filmId;
    @NotNull
    private Integer userId;
    private Integer useful = 0;
}
