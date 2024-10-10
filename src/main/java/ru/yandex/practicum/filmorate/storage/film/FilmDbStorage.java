package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {
    static Logger log = LoggerFactory.getLogger(FilmDbStorage.class.getName());
    static String FIND_ALL_QUERY = "SELECT * FROM films";
    static String CREATE_FILM_QUERY = "INSERT INTO films (name, description, releasedate, duration) " +
            "VALUES (?, ?, ?, ?)";
    static String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, releasedate = ?, " +
            "duration = ? WHERE film_id = ?";
    static String GET_FILM_QUERY = "SELECT * FROM films WHERE film_id = ?";
    static String FIND_MOST_POPULAR_QUERY = "SELECT f.* FROM film_likes AS fl " +
            "JOIN films AS f ON fl.film_id=f.film_id " +
            "GROUP BY fl.film_id ORDER BY COUNT(fl.user_id) DESC LIMIT ?";
    static String ADD_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    static String REMOVE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    static String GET_FILM_LIKES_QUERY = "SELECT user_id FROM film_likes WHERE film_id = ?";
    static String GET_FILM_LIKED_BY_USER_ID = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration " +
            "FROM films f " +
            "JOIN film_likes fl ON f.film_id = fl.film_id " +
            "WHERE fl.user_id = ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, UserStorage userStorage) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> findAll() {
        log.debug("Получаем список всех фильмов.");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film create(CreateFilmDto film) {
        log.debug("Добавляем новый фильм в базу данных.");
        Long key = insert(CREATE_FILM_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration());
        return getFilmById(key);
    }

    @Override
    public Film update(FilmDto newFilm) {
        log.debug("Обновляем данные фильма в базе данных.");
        update(UPDATE_FILM_QUERY, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(),
                newFilm.getDuration(), newFilm.getId());
        return getFilmById(newFilm.getId());
    }

    @Override
    public Film getFilmById(Long filmId) {
        log.debug("Получаем данные фильма по его id.");
        return findOne(GET_FILM_QUERY, filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id " + filmId + " не найден."));
    }

    @Override
    public Collection<Film> findMostPopularFilms(int count) {
        log.debug("Получаем список наиболее популярных фильмов.");
        return findMany(FIND_MOST_POPULAR_QUERY, count);
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        log.debug("Добавляем фильму лайк.");
        update(ADD_LIKE_QUERY, filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        log.debug("Удаляем у фильма лайк.");
        update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public Set<Long> getFilmLikes(Long filmId) {
        log.debug("Получаем список всех лайков определенного фильма.");
        return new HashSet<>(jdbc.query(GET_FILM_LIKES_QUERY,
                (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }

    @Override
    public Collection<Film> getFilmLikedByUserId(Long userId) {
        log.debug("Получение фильмов отмеченных лайком пользователя c id {}.", userId);
        return findMany(GET_FILM_LIKED_BY_USER_ID, userId);
    }
}
