package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SearchFilmRequest {
    @NotNull
    @Size(min = 2, max = 50)
    private String query;

    @Pattern(regexp = "title|director|title,director|director,title")
    private String by;
}

