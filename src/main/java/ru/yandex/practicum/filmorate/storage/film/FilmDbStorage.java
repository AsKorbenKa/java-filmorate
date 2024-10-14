package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.film.mapper.SelectedFilmsRowMapper;

import java.util.*;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {
    static Logger log = LoggerFactory.getLogger(FilmDbStorage.class.getName());
    SelectedFilmsRowMapper selectedMapper;
    static String FIND_ALL_QUERY = "SELECT * FROM films";
    static String CREATE_FILM_QUERY = "INSERT INTO films (name, description, releasedate, duration) " +
            "VALUES (?, ?, ?, ?)";
    static String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, releasedate = ?, " +
            "duration = ? WHERE film_id = ?";
    static String GET_FILM_QUERY = "SELECT * FROM films WHERE film_id = ?";
    static String FIND_MOST_POPULAR_QUERY =  "SELECT f.* " +
            "FROM films AS f " +
            "LEFT JOIN film_likes AS fl ON f.film_id = fl.film_id " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(fl.user_id) DESC " +
            "LIMIT ?;";
    static String ADD_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    static String REMOVE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    static String GET_FILM_LIKES_QUERY = "SELECT user_id FROM film_likes WHERE film_id = ?";
    static String GET_FILMS_SORTED_BY_YEAR = """
            SELECT f.*
            FROM films AS f
            JOIN film_directors AS fd ON f.film_id = fd.film_id\s
            WHERE fd.director_id = ?
            ORDER BY EXTRACT(YEAR FROM f.releaseDate)""";
    static String GET_FILMS_SORTED_BY_LIKES = """
            SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration
            FROM film_directors AS fd
            LEFT JOIN films AS f ON fd.film_id = f.film_id
            LEFT JOIN FILM_LIKES AS fl ON fl.film_id = f.film_id
            WHERE fd.director_id = ?
            GROUP BY f.film_id, f.name, f.description, f.releaseDate, f.duration
            ORDER BY COUNT(fl.user_id) DESC""";
    static String GET_FILM_LIKED_BY_USER_ID = "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration " +
            "FROM films f " +
            "JOIN film_likes fl ON f.film_id = fl.film_id " +
            "WHERE fl.user_id = ?";
    static String GET_SORTED_BY_GENRE_AND_YEAR = """
            SELECT f.*
            FROM FILMS f
            JOIN FILM_GENRES fg ON f.FILM_ID = fg.FILM_ID\s
            JOIN FILM_LIKES fl ON f.FILM_ID = fl.FILM_ID\s
            WHERE fg.GENRE_ID = ? AND EXTRACT(YEAR FROM f.releaseDate) = ?
            GROUP BY f.FILM_ID\s
            ORDER BY COUNT(fl.USER_ID) DESC
            LIMIT ?""";
    static String GET_SORTED_BY_YEAR = """
            SELECT f.*
            FROM FILMS f
            JOIN FILM_LIKES fl ON f.FILM_ID = fl.FILM_ID\s
            WHERE EXTRACT(YEAR FROM f.releaseDate) = ?
            GROUP BY f.FILM_ID\s
            ORDER BY COUNT(fl.USER_ID) DESC
            LIMIT ?""";
    static String GET_SORTED_BY_GENRE = """
            SELECT f.*
            FROM FILMS f
            JOIN FILM_GENRES fg ON f.FILM_ID = fg.FILM_ID\s
            JOIN FILM_LIKES fl ON f.FILM_ID = fl.FILM_ID\s
            WHERE fg.GENRE_ID = ?
            GROUP BY f.FILM_ID\s
            ORDER BY COUNT(fl.USER_ID) DESC
            LIMIT ?""";
    static String DELETE_FILM_QUERY = "DELETE FROM films WHERE film_id = ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, SelectedFilmsRowMapper selectedMapper) {
        super(jdbc, mapper);
        this.selectedMapper = selectedMapper;
    }

    @Override
    public Collection<Film> findAll() {
        log.debug("Получаем список всех фильмов.");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film create(FilmDto film) {
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

    public Map<Long, Film> getFilmsById(Collection<Long> filmIds) {
        StringBuilder getFilmsByIdQuery = new StringBuilder("SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, " +
                "f.DURATION, g.NAME AS GENRE, g.GENRE_ID, fr.NAME AS RATING, fr.RATING_ID, fd.DIRECTOR_ID," +
                " d.NAME AS DIRECTOR " +
                "FROM FILMS f " +
                "LEFT JOIN FILM_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN MPA_RATINGS mr ON f.FILM_ID = mr.FILM_ID " +
                "LEFT JOIN FILM_RATING fr ON mr.RATING_ID = fr.RATING_ID " +
                "LEFT JOIN FILM_DIRECTORS fd ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE f.FILM_ID IN (");

        for (int i = 0; i < filmIds.size(); i++) {
            getFilmsByIdQuery.append("?");
            if (i < filmIds.size() - 1) {
                getFilmsByIdQuery.append(", ");
            }
        }
        getFilmsByIdQuery.append(")");
        Object[] params = filmIds.toArray();
        return jdbc.query(getFilmsByIdQuery.toString(), selectedMapper, params).getFirst();
    }

    @Override
    public Collection<Film> findMostPopularFilms(int count, Long genreId, Integer year) {

        if (genreId == null && year == null) {
            log.debug("Получаем список наиболее популярных фильмов.");
            return findMany(FIND_MOST_POPULAR_QUERY, count);
        } else if (genreId != null) {
            if (year == null) {
                log.debug("Получаем список наиболее популярных фильмов, отсортированных по жанру.");
                return findMany(GET_SORTED_BY_GENRE, genreId, count);
            } else {
                log.debug("Получаем список наиболее популярных фильмов, отсортированных по жанру и году выпуска.");
                return findMany(GET_SORTED_BY_GENRE_AND_YEAR, genreId, year, count);
            }
        }

        log.debug("Получаем список наиболее популярных фильмов, году выпуска.");
        return findMany(GET_SORTED_BY_YEAR, year, count);
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
    public Collection<Film> findSortedDirectorFilms(Long directorId, String sortBy) {
        log.debug("Получаем отсортированный по годам или лайкам список фильмов.");
        if (sortBy.equals("year")) {
            return findMany(GET_FILMS_SORTED_BY_YEAR, directorId);
        }
        return findMany(GET_FILMS_SORTED_BY_LIKES, directorId);
    }

    @Override
    public Collection<Film> getFilmLikedByUserId(Long userId) {
        log.debug("Получение фильмов отмеченных лайком пользователя c id {}.", userId);
        return findMany(GET_FILM_LIKED_BY_USER_ID, userId);
    }

    @Override
    public Collection<Film> search(String title, String director) {
        StringBuilder sql = new StringBuilder("SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, " +
                "f.DURATION, g.NAME AS GENRE, g.GENRE_ID, fr.NAME AS RATING, fr.RATING_ID, fd.DIRECTOR_ID," +
                " d.NAME AS DIRECTOR " +
                "FROM FILMS f " +
                "LEFT JOIN FILM_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "LEFT JOIN MPA_RATINGS mr ON f.FILM_ID = mr.FILM_ID " +
                "LEFT JOIN FILM_RATING fr ON mr.RATING_ID = fr.RATING_ID " +
                "LEFT JOIN FILM_DIRECTORS fd ON f.FILM_ID = fd.FILM_ID " +
                "LEFT JOIN DIRECTORS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE ");

        if (title != null) {
            sql.append("LOWER(f.NAME) LIKE '%").append(title.toLowerCase()).append("%'");
            if (director != null) {
                sql.append(" OR ");
            }
        }
        if (director != null) {
            sql.append("LOWER(d.NAME) LIKE '%").append(director.toLowerCase()).append("%'");
        }

        List<Map<Long, Film>> result = jdbc.query(sql.toString(), selectedMapper);
        if (!result.isEmpty()) {
            return result.getFirst().values();
        }
        return new ArrayList<>();
    }

    @Override
    public void delete(Long id) {
        log.debug("Удаляем фильм.");
        update(DELETE_FILM_QUERY, id);
    }
}
