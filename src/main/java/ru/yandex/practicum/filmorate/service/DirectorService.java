package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DirectorService {
    DirectorStorage directorStorage;
    FilmStorage filmStorage;

    public Collection<Director> findAllDirectors() {
        return directorStorage.findAllDirectors();
    }

    public Director getDirectorById(Long directorId) {
        return directorStorage.getDirectorById(directorId);
    }

    public Director create(Director director) {
        return directorStorage.create(director);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }

    public void delete(Long directorId) {
        directorStorage.delete(directorId);
    }
}
