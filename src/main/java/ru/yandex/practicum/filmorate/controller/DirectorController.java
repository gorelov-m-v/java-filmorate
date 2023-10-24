package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService service;


    @GetMapping
    public List<Director> getDirectors(){
        return service.getAllDirectors();
    }


    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable Integer id){
        return service.getDirectorById(id);
    }


    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director){
        return service.createDirector(director);
    }


    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director){
        return service.updateDirector(director);
    }


    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable Integer id){
        service.deleteDirectorById(id);
    }
}
