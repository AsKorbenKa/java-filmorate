package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.dto.NewGenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface GenreStorage {
    Collection<Genre> getGenres();

    Set<Genre> getFilmGenres(Long filmId);

    Genre getGenreById(Long genreId);

    Genre create(NewGenreDto newGenreDto);

    void delete(Long genreId);

    void createGenreAndFilmConn(Long filmId, Set<Genre> genres);
}
