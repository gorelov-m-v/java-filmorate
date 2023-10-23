package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RateMPA;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService service;


    @GetMapping
    public List<RateMPA> getAllMpa() {
        log.info("Запрос на получение списка всех рейтингов");
        List<RateMPA> rate = service.getAllRatings();
        log.info("Список рейтингов отправлен");
        return rate;
    }

    @GetMapping("/{id}")
    public RateMPA getMpaById(@PathVariable("id") Integer id) {
        log.info("Запрос на получение рейтинга по id");
        RateMPA rate = service.getMpaById(id);
        log.info("Рейтинг отправлен");
        return rate;
    }
}
