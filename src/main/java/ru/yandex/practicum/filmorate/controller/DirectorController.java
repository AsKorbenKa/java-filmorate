package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DirectorController {
    DirectorService directorService;

    @GetMapping
    public Collection<Director> findAllDirectors() {
        return directorService.findAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable("id") Long directorId) {
        return directorService.getDirectorById(directorId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long directorId) {
        directorService.delete(directorId);
    }
}
