package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(FilmDto film);

    Film update(FilmDto newFilm);

    Film getFilmById(Long filmId);

    Collection<Film> findMostPopularFilms(int count, Long genreId, Integer year);

    Film addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    Set<Long> getFilmLikes(Long filmId);

    Collection<Film> getFilmLikedByUserId(Long userId);

    Collection<Film> findSortedDirectorFilms(Long directorId, String sortBy);

    Collection<Film> search(String title, String director);

    void delete(Long id);
}
