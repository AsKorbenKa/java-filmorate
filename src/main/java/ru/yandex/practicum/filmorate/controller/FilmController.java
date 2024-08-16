package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class.getName());

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Создаем запись о фильме");
        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новый фильм в памяти приложения
        films.put(film.getId(), film);
        log.trace("Фильм " + film.getName() + " успешно сохранен");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Изменяем запись о фильме");
        if (newFilm.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            films.put(newFilm.getId(), newFilm);
        } else {
            log.error("Фильм со следующим id не найден: " + newFilm.getId());
            throw new ValidationException("Фильм со следующим id не найден: " + newFilm.getId());
        }
        log.trace("Фильм " + newFilm.getName() + " успешно обновлен");
        return newFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
