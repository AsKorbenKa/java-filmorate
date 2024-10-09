package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaRatingStorage mpaRatingStorage;
    private final GenreStorage genreStorage;
    private final UserStorage userStorage;

    public Collection<FilmDto> findAll() {
        return filmStorage.findAll().stream()
                .map(film -> FilmMapper.filmDtoMapper(film, mpaRatingStorage.getFilmMpaRating(film.getId()),
                        genreStorage.getFilmGenres(film.getId())))
                .collect(Collectors.toList());
    }

    public FilmDto getFilmById(Long filmId) {
        return FilmMapper.filmDtoMapper(filmStorage.getFilmById(filmId), mpaRatingStorage.getFilmMpaRating(filmId),
                genreStorage.getFilmGenres(filmId));
    }

    public FilmDto create(CreateFilmDto createFilmDto) {
        Film film = filmStorage.create(createFilmDto);

        if (!(createFilmDto.getMpa() == null) && createFilmDto.getMpa().getId() != 0) {
            mpaRatingStorage.createMpaAndFilmConn(film.getId(), createFilmDto.getMpa().getId());
        }
        if (!(createFilmDto.getGenres() == null) && !createFilmDto.getGenres().isEmpty()) {
            genreStorage.createGenreAndFilmConn(film.getId(), createFilmDto.getGenres());
        }

        return FilmMapper.filmDtoMapper(film, mpaRatingStorage.getFilmMpaRating(film.getId()),
                genreStorage.getFilmGenres(film.getId()));
    }

    public FilmDto update(FilmDto newFilm) {
        Film film = filmStorage.update(newFilm);

        if (!(newFilm.getMpa() == null) && newFilm.getMpa().getId() != 0) {
            mpaRatingStorage.createMpaAndFilmConn(film.getId(), newFilm.getMpa().getId());
        }
        if (!(newFilm.getGenres() == null) && !newFilm.getGenres().isEmpty()) {
            genreStorage.createGenreAndFilmConn(film.getId(), newFilm.getGenres());
        }

        return FilmMapper.filmDtoMapper(film, mpaRatingStorage.getFilmMpaRating(film.getId()),
                genreStorage.getFilmGenres(film.getId()));
    }

    public Collection<FilmDto> findMostPopularFilms(int count) {
        return filmStorage.findMostPopularFilms(count).stream()
                .map(film -> FilmMapper.filmDtoMapper(film, mpaRatingStorage.getFilmMpaRating(film.getId()),
                        genreStorage.getFilmGenres(film.getId())))
                .collect(Collectors.toList());
    }

    public FilmDto addLike(Long filmId, Long userId) {
        return FilmMapper.filmDtoMapper(filmStorage.addLike(filmId, userId), mpaRatingStorage.getFilmMpaRating(filmId),
                genreStorage.getFilmGenres(filmId));
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.removeLike(filmId, userId);
    }

    //получение общих фильмов пользователя и его друга
    public Collection<FilmDto> commonFilms(Long userId, Long friendId) {
        log.debug("Получаем общие фильмы для пользователей с id: {} и {}", userId, friendId);

        //если пользователь не найден, выбрасывается исключение
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        //если у кого-то из пользователей нет лайков к фильмам, выбрасываем исключение
        Collection<Film> userFilms = filmStorage.getFilmByUserId(userId);
        Collection<Film> friendFilms = filmStorage.getFilmByUserId(friendId);

        //поиск пересечения по фильмам
        Set<Film> userFilmSet = new HashSet<>(userFilms);
        userFilmSet.retainAll(friendFilms);

        log.debug("Найдено общих фильмов {} .", userFilmSet.size());
        return userFilmSet.stream()
                .map(film -> FilmMapper.filmDtoMapper(film, mpaRatingStorage.getFilmMpaRating(film.getId()),
                        genreStorage.getFilmGenres(film.getId())))
                .collect(Collectors.toList());
    }

}
