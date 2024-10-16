package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.enums.SortBy;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {
    FilmService filmService;
    RecommendationService recService;

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable("id") Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> findMostPopularFilms(@RequestParam(name = "count", defaultValue = "10")
                                                        @Positive int count,
                                            @RequestParam(name = "genreId", required = false) Long genreId,
                                            @RequestParam(name = "year", required = false) Integer year) {
        return filmService.findMostPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public Collection<FilmDto> findDirectorFilms(@PathVariable("directorId") Long directorId,
                                                 @RequestParam(defaultValue = "year") String sortBy) {
        if (sortBy.equals(SortBy.YEAR.toString().toLowerCase()) || sortBy.equals(SortBy.LIKES.toString().toLowerCase())) {
            return filmService.findSortedDirectorFilms(directorId, sortBy);
        } else {
            throw new ParameterNotValidException("Ошибка при поиске отсортированного списка фильмов режиссера. " +
                    "Параметр sortBy должен быть равен либо 'year', либо 'likes'.");
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody FilmDto filmDto) {
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

    @GetMapping("/search")
    public Collection<FilmDto> search(@RequestParam("query") String searchString,
                                      @RequestParam("by") List<String> params) {
        return filmService.search(searchString, new HashSet<>(params));
    }

    @DeleteMapping("/{filmId}")
    public FilmDto delete(@PathVariable("filmId") Long id) {
        return filmService.delete(id);
    }

}
