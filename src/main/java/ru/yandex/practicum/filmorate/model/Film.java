package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.validator.MinimumDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Slf4j
public class Film {
    private Integer id;
    @NotEmpty(message = "название не может быть пустым")
    private String name;
    @Size(max = 200, message = "длина описания не может быть больше 200 символов")
    private String description;
    @NotNull(message = "дата не может быть null")
    @MinimumDate
    private LocalDate releaseDate;
    @Positive(message = "продолжительность фильма должна быть положительной")
    private int duration;
    private int likes;
    private RateMPA mpa;
    private List<Genre> genres = new ArrayList<>();
}
