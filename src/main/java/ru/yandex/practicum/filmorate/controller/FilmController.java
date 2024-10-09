package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {
    FilmService filmService;

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable("id") Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> findMostPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.findMostPopularFilms(count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody CreateFilmDto filmDto) {
        return filmService.create(filmDto);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody FilmDto newFilm) {
        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmDto addLike(@PathVariable("id") Long id,
                           @PathVariable("userId") Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable("id") Long id,
                           @PathVariable("userId") Long userId) {
        filmService.removeLike(id, userId);
    }

    //получение общих фильмов пользователя и его друга
    @GetMapping("/common")
    public Collection<FilmDto> commonFilms(@RequestParam("userId") Long userId,
                                           @RequestParam("friendId") Long friendId) {
        return filmService.commonFilms(userId, friendId);
    }
}
