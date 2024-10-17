package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class FilmService {
    FilmStorage filmStorage;
    MpaRatingStorage mpaRatingStorage;
    GenreStorage genreStorage;
    FeedStorage feedStorage;
    DirectorStorage directorStorage;
    UserStorage userStorage;
    RecommendationService recService;

    public Collection<FilmDto> findAll() {
        return filmStorage.findAll().stream()
                .map(film -> FilmMapper.filmDtoMapper(film, mpaRatingStorage.getFilmMpaRating(film.getId()),
                        genreStorage.getFilmGenres(film.getId()), directorStorage.getDirectorOfTheFilm(film.getId())))
                .collect(Collectors.toList());
    }

    public FilmDto getFilmById(Long filmId) {
        return FilmMapper.filmDtoMapper(filmStorage.getFilmById(filmId), mpaRatingStorage.getFilmMpaRating(filmId),
                genreStorage.getFilmGenres(filmId), directorStorage.getDirectorOfTheFilm(filmId));
    }

    public FilmDto create(FilmDto createFilmDto) {
        Film film = filmStorage.create(createFilmDto);

        mpaRatingStorage.createMpaAndFilmConn(film.getId(), createFilmDto.getMpa());
        genreStorage.createGenreAndFilmConn(film.getId(), createFilmDto.getGenres());
        directorStorage.createFilmAndDirConn(film.getId(), createFilmDto.getDirectors());

        return FilmMapper.filmDtoMapper(film, mpaRatingStorage.getFilmMpaRating(film.getId()),
                genreStorage.getFilmGenres(film.getId()), directorStorage.getDirectorOfTheFilm(film.getId()));
    }

    public FilmDto update(FilmDto newFilm) {
        Film film = filmStorage.update(newFilm);

        mpaRatingStorage.createMpaAndFilmConn(film.getId(), newFilm.getMpa());
        genreStorage.createGenreAndFilmConn(film.getId(), newFilm.getGenres());
        directorStorage.createFilmAndDirConn(film.getId(), newFilm.getDirectors());

        return FilmMapper.filmDtoMapper(film, mpaRatingStorage.getFilmMpaRating(film.getId()),
                genreStorage.getFilmGenres(film.getId()), directorStorage.getDirectorOfTheFilm(film.getId()));
    }

    public Collection<FilmDto> findMostPopularFilms(int count, Long genreId, Integer year) {
        // Проверяем жанр на наличие в бд, иначе выбрасываем ошибку
        if (genreId != null) {
            genreStorage.getGenreById(genreId);
        }

        return filmStorage.findMostPopularFilms(count, genreId, year).stream()
                .map(film -> FilmMapper.filmDtoMapper(film, mpaRatingStorage.getFilmMpaRating(film.getId()),
                        genreStorage.getFilmGenres(film.getId()), directorStorage.getDirectorOfTheFilm(film.getId())))
                .collect(Collectors.toList());
    }

    public FilmDto addLike(Long filmId, Long userId) {
        // Проверяем есть ли пользователь и фильма в бд
        isFilmExists(filmId);
        isUserExists(userId);

        //добавление в ленту событий
        feedStorage.addFeed(filmId, userId, EventType.LIKE, Operation.ADD);
        return FilmMapper.filmDtoMapper(filmStorage.addLike(filmId, userId), mpaRatingStorage.getFilmMpaRating(filmId),
                genreStorage.getFilmGenres(filmId), directorStorage.getDirectorOfTheFilm(filmId));
    }

    public void removeLike(Long filmId, Long userId) {
        // Проверяем есть ли пользователь и фильма в бд
        isFilmExists(filmId);
        isUserExists(userId);

        filmStorage.removeLike(filmId, userId);
        //добавление в ленту событий
        feedStorage.addFeed(filmId, userId, EventType.LIKE, Operation.REMOVE);
    }

    public Collection<FilmDto> findSortedDirectorFilms(Long directorId, String sortBy) {
        return filmStorage.findSortedDirectorFilms(directorId, sortBy).stream()
                .map(film -> FilmMapper.filmDtoMapper(film, mpaRatingStorage.getFilmMpaRating(film.getId()),
                        genreStorage.getFilmGenres(film.getId()), directorStorage.getDirectorOfTheFilm(film.getId())))
                .collect(Collectors.toList());
    }

    //получение общих фильмов пользователя и его друга
    public Collection<FilmDto> commonFilms(Long userId, Long friendId) {
        log.debug("Получаем общие фильмы для пользователей с id: {} и {}", userId, friendId);

        //если пользователь не найден, выбрасывается исключение
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        //если у кого-то из пользователей нет лайков к фильмам, выбрасываем исключение
        Collection<Film> userFilms = filmStorage.getFilmLikedByUserId(userId);
        Collection<Film> friendFilms = filmStorage.getFilmLikedByUserId(friendId);

        //поиск пересечения по фильмам
        Set<Film> userFilmSet = new HashSet<>(userFilms);
        userFilmSet.retainAll(friendFilms);

        log.debug("Найдено общих фильмов {} .", userFilmSet.size());
        return userFilmSet.stream()
                .map(film -> FilmMapper.filmDtoMapper(film, mpaRatingStorage.getFilmMpaRating(film.getId()),
                        genreStorage.getFilmGenres(film.getId()), directorStorage.getDirectorOfTheFilm(film.getId())))
                .collect(Collectors.toList());
    }

    public List<FilmDto> search(String searchString, HashSet<String> params) {
        String title;
        String director;
        title = params.contains("title") ? searchString : null;
        director = params.contains("director") ? searchString : null;
        return filmStorage.search(title, director).stream()
                .map(FilmMapper::fullFilmDtoMapper)
                .toList();
    }

    public FilmDto delete(Long id) {
        FilmDto film = getFilmById(id);
        filmStorage.delete(id);
        return film;
    }

    private void isFilmExists(Long filmId) {
        filmStorage.getFilmById(filmId);
    }

    private void isUserExists(Long userId) {
        userStorage.getUserById(userId);
    }
}
