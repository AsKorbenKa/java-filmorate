package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class.getName());

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film create(Film film) {
        log.info("Создаем запись о фильме");
        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новый фильм в памяти приложения
        films.put(film.getId(), film);
        log.trace("Фильм " + film.getName() + " успешно сохранен");
        return film;
    }

    public Film update(Film newFilm) {
        log.info("Изменяем запись о фильме");
        if (newFilm.getId() == null) {
            log.error("Id должен быть указан");
            throw new ParameterNotValidException("Id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            films.put(newFilm.getId(), newFilm);
        } else {
            log.error("Фильм со следующим id не найден: " + newFilm.getId());
            throw new FilmNotFoundException("Фильм со следующим id не найден: " + newFilm.getId());
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
