package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.NewGenreDto;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreDbStorage extends BaseStorage<Genre> implements GenreStorage {
    static Logger log = LoggerFactory.getLogger(GenreDbStorage.class.getName());
    static String GET_GENRES_QUERY = "SELECT * FROM genre";
    static String GET_FILM_GENRES_QUERY = """
            SELECT g.* FROM genre g\s
            JOIN film_genres fg ON g.genre_id=fg.genre_id\s
            WHERE fg.film_id = ?\s
            ORDER BY g.genre_id ASC""";
    static String GET_GENRE_BY_ID_QUERY = "SELECT * FROM genre WHERE genre_id = ?";
    static String CREATE_GENRE_QUERY = "INSERT INTO genre (name) VALUES (?)";
    static String DELETE_GENRE_QUERY = "DELETE FROM genre WHERE genre_id = ?";
    static String CREATE_GENRE_AND_FILM_CONN = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    static String IS_GENRE_EXISTS_QUERY = """
            SELECT *
            FROM genre
            WHERE genre_id IN (SELECT MAX(genre_id)
            FROM genre)""";
    static String IS_GENRE_AND_FILM_EXISTS_QUERY = "SELECT genre_id FROM film_genres WHERE (film_id = ? AND genre_id = ?)";
    static String REMOVE_ALL_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> getGenres() {
        log.debug("Получаем список всех жанров.");
        return findMany(GET_GENRES_QUERY);
    }

    @Override
    public Set<Genre> getFilmGenres(Long filmId) {
        log.debug("Получаем список всех жанров определенного фильма.");
        return new LinkedHashSet<>(findMany(GET_FILM_GENRES_QUERY, filmId));
    }

    @Override
    public Genre getGenreById(Long genreId) {
        log.debug("Получаем жанр по его id.");
        return findOne(GET_GENRE_BY_ID_QUERY, genreId)
                .orElseThrow(() -> new GenreNotFoundException("Жанр фильма с id " + genreId + " не найден."));
    }

    @Override
    public Genre create(NewGenreDto newGenreDto) {
        log.debug("Создаем новый жанр.");
        Long key = insert(CREATE_GENRE_QUERY, newGenreDto.getName());
        return getGenreById(key);
    }

    @Override
    public void delete(Long genreId) {
        log.debug("Удаляем жанр.");
        update(DELETE_GENRE_QUERY, genreId);
    }

    @Override
    public void createGenreAndFilmConn(Long filmId, Set<Genre> genres) {
        log.debug("Объединяем фильм и его жанры по их id.");
        update(REMOVE_ALL_FILM_GENRES_QUERY, filmId);

        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                isGenreExists(genre.getId());
                if (!isGenreAndFilmExists(filmId, genre.getId()).isEmpty()) {
                    continue;
                }
                try {
                    insert(CREATE_GENRE_AND_FILM_CONN, filmId, genre.getId());
                } catch (InvalidDataAccessApiUsageException ignored) {
                }
            }
        }
    }

    private void isGenreExists(Long genreId) {
        log.debug("Проверяем жанр на его существование в базе данных.");
        Genre maxGenre = findOne(IS_GENRE_EXISTS_QUERY)
                .orElseThrow(() -> new GenreNotFoundException("Не удалось получить жанр с максимальным id. " +
                "Список жанров пуст."));
        if (maxGenre.getId() < genreId) {
            throw new ParameterNotValidException("В базе данных нет жанра фильма с id " + genreId + ".");
        }
    }

    private List<Long> isGenreAndFilmExists(Long filmId, Long genreId) {
        log.debug("Проверяем жанр и фильм на их существование в базе данных.");
        return jdbc.query(IS_GENRE_AND_FILM_EXISTS_QUERY,
                (rs, rowNum) -> rs.getLong("genre_id"), filmId, genreId);
    }
}
