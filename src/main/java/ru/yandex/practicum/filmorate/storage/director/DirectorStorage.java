package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Set;

public interface DirectorStorage {
    Collection<Director> findAllDirectors();

    Director getDirectorById(Long directorId);

    Set<Director> getDirectorOfTheFilm(Long filmId);

    Director create(Director director);

    Director update(Director director);

    void delete(Long directorId);

    void createFilmAndDirConn(Long filmId, Set<Director> directors);
}
