package ru.yandex.practicum.filmorate.storage.rating;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.NewMpaRatingDto;
import ru.yandex.practicum.filmorate.exception.ParameterNotValidException;
import ru.yandex.practicum.filmorate.exception.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MpaRatingDbStorage extends BaseStorage<MpaRating> implements MpaRatingStorage {
    static Logger log = LoggerFactory.getLogger(MpaRatingDbStorage.class.getName());
    static String GET_MPA_QUERY = "SELECT * FROM film_rating";
    static String GET_FILM_MPA_QUERY = """
            SELECT fr.*
            FROM FILM_RATING fr\s
            JOIN MPA_RATINGS mr ON fr.RATING_ID = mr.RATING_ID\s
            WHERE mr.FILM_ID = ?""";
    static String GET_RATING_BY_ID_QUERY = "SELECT * FROM film_rating WHERE rating_id = ?";
    static String CREATE_MPA_QUERY = "INSERT INTO film_rating (name) VALUES (?)";
    static String DELETE_MPA_QUERY = "DELETE FROM film_rating WHERE rating_id = ?";
    static String CREATE_MPA_AND_FILM_CONN = "INSERT INTO mpa_ratings (film_id, rating_id) VALUES (?, ?)";
    static String IS_MPA_EXISTS_QUERY = """
            SELECT *
            FROM FILM_RATING
            WHERE RATING_ID IN (SELECT MAX(RATING_ID)
            FROM FILM_RATING)""";
    static String IS_MPA_AND_FILM_EXISTS_QUERY = "SELECT * FROM film_rating AS fr " +
            "JOIN mpa_ratings AS mr ON fr.rating_id = mr.rating_id " +
            "WHERE mr.film_id = ?";

    public MpaRatingDbStorage(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<MpaRating> getAllMpaRatings() {
        log.debug("Получаем список всех рейтингов фильмов.");
        return findMany(GET_MPA_QUERY);
    }

    @Override
    public MpaRating getRatingById(Long ratingId) {
        log.debug("Получаем рейтинг по его id.");
        return findOne(GET_RATING_BY_ID_QUERY, ratingId)
                .orElseThrow(() -> new RatingNotFoundException("Ошибка при поиске рейтинга по id. " +
                        "Рейтинг с id " + ratingId + " не найден."));
    }

    @Override
    public MpaRating getFilmMpaRating(Long filmId) {
        log.debug("Получаем рейтинг определенного фильма по его id.");
        return findOne(GET_FILM_MPA_QUERY, filmId)
                .orElse(new MpaRating());
    }

    @Override
    public MpaRating create(NewMpaRatingDto newMpaRatingDto) {
        log.debug("Создаем новый рейтинг.");
        Long key = insert(CREATE_MPA_QUERY, newMpaRatingDto.getName());
        return getRatingById(key);
    }

    @Override
    public void delete(Long ratingId) {
        log.debug("Удаляем рейтинг.");
        update(DELETE_MPA_QUERY, ratingId);
    }

    @Override
    public void createMpaAndFilmConn(Long filmId, Long ratingId) {
        log.debug("Объединяем фильм и его рейтинг по их id.");
        isRatingExists(ratingId);
        if (isMpaAndFilmExists(filmId).isPresent()) {
            update("DELETE FROM mpa_ratings WHERE film_id = ?", filmId);
        }

        try {
            insert(CREATE_MPA_AND_FILM_CONN, filmId, ratingId);
            log.debug("Объединение фильма и рейтинга по id успешно совершено.");
        } catch (InvalidDataAccessApiUsageException ignored) {
        }
    }

    private void isRatingExists(Long ratingId) {
        log.debug("Проверяем есть ли такой рейтинг в базе данных.");
        MpaRating maxMpaRating = findOne(IS_MPA_EXISTS_QUERY)
                .orElseThrow(() -> new RatingNotFoundException("Не удалось получить рейтинг с максимальным id. " +
                        "Список пуст."));
        if (maxMpaRating.getId() < ratingId) {
            throw new ParameterNotValidException("В базе данных нет рейтинга фильма с id " + ratingId + ".");
        }
    }

    private Optional<MpaRating> isMpaAndFilmExists(Long filmId) {
        log.debug("Проверяем фильм и рейтинг на наличие в базе данных.");
        return findOne(IS_MPA_AND_FILM_EXISTS_QUERY, filmId);
    }
}
