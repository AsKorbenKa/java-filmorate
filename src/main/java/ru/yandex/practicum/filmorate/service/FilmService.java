package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage inMemoryFilmStorage;
    private final UserStorage inMemoryUserStorage;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class.getName());

    public List<Film> findMostPopularFilms(int count) {
        log.info("Создаем список самых популярных фильмов по количеству лайков.");
        // создаем список от большего количества лайков к меньшему
        List<Film> films = inMemoryFilmStorage.findAll().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size()))
                .toList().reversed();

        log.trace("Список самых популярных фильмов успешно составлен");
        if (films.size() <= count) {
            return films;
        } else {
            return films.subList(0, count);
        }
    }

    public Film addLike(Long filmId, Long userId) {
        log.info("Добавляем лайк указанному фильму.");
        Film film = checkFilmAndUserExist(filmId, userId, "добавлении лайка фильму");
        if (film.getLikes().contains(userId)) {
            log.error("Пользователь с id " + userId + " уже поставил лайк фильму с id " + filmId + ".");
            throw new ConditionsNotMetException(
                    "Пользователь с id " + userId + " уже поставил лайк фильму с id " + filmId + ".");
        } else {
            film.getLikes().add(userId);
        }
        log.trace("Лайк фильму успешно поставлен.");
        return film;
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Удаляем лайк у фильма.");
        Film film = checkFilmAndUserExist(filmId, userId, "удалении лайка");
        if (!film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
        } else {
            log.error("Ошибка при удалении лайка. " +
                    "У фильма с id " + filmId + " нет лайка от пользователя с id " + userId + ".");
            throw new ConditionsNotMetException("Ошибка при удалении лайка. " +
                    "У фильма с id " + filmId + " нет лайка от пользователя с id " + userId + ".");
        }
        log.trace("Лайк успешно удален.");
    }

    private Film checkFilmAndUserExist(Long filmId, Long userId, String message) {
        log.info("Проверяем существует ли фильм и пользователь с соответствующими id.");
        Optional<Film> filmToCheck = inMemoryFilmStorage.findAll().stream()
                .filter(filmInMap -> Objects.equals(filmInMap.getId(), filmId))
                .findFirst();

        boolean userToCheck = inMemoryUserStorage.findAll().stream()
                .filter(userInMap -> Objects.equals(userInMap.getId(), userId))
                .findFirst().isEmpty();

        if (filmToCheck.isEmpty() && userToCheck) {
            log.error("Ошибка при " + message + ". " +
                    "Фильм с id " + filmId + " и пользователь с id " + userId + " не найдены.");
            throw new FilmNotFoundException("Ошибка при " + message + ". " +
                    "Фильм с id " + filmId + " и пользователь с id " + userId + " не найдены.");
        }
        if (filmToCheck.isEmpty()) {
            log.error("Ошибка при " + message + ". Фильм с id " + userId + " не найден.");
            throw new FilmNotFoundException("Ошибка при " + message + ". Фильм с id " + userId + " не найден.");
        }
        if (userToCheck) {
            log.error("Ошибка при " + message + ". Пользователь с id " + userId + " не найден.");
            throw new UserNotFoundException("Ошибка при " + message + ". Пользователь с id " + userId + " не найден.");
        }
        log.trace("Данные фильма и пользователя были успешно найдены.");
        return filmToCheck.get();
    }
}
