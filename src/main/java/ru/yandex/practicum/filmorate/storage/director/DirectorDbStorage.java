package ru.yandex.practicum.filmorate.storage.director;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DirectorDbStorage extends BaseStorage<Director> implements DirectorStorage {
    static Logger log = LoggerFactory.getLogger(DirectorDbStorage.class.getName());
    static String FIND_ALL_QUERY = "SELECT * FROM directors";
    static String GET_DIR_OF_THE_FILM_QUERY = "SELECT d.* FROM directors as d " +
            "JOIN film_directors AS fd ON d.director_id=fd.director_id " +
            "WHERE fd.film_id = ?";
    static String GET_DIR_BY_ID_QUERY = "SELECT * FROM directors WHERE director_id = ?";
    static String CREATE_QUERY = "INSERT INTO directors (name) VALUES (?)";
    static String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE director_id = ?";
    static String DELETE_QUERY = "DELETE FROM directors WHERE director_id = ?";
    static String CREATE_FILM_AND_DIR_CONN = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Director> findAllDirectors() {
        log.debug("Получаем список всех режиссеров.");
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Set<Director> getDirectorOfTheFilm(Long filmId) {
        log.debug("Получаем режиссера определенного фильма.");
        return new LinkedHashSet<>(findMany(GET_DIR_OF_THE_FILM_QUERY, filmId));
    }

    @Override
    public Director getDirectorById(Long directorId) {
        log.debug("Получаем режиссера по его id.");
        return findOne(GET_DIR_BY_ID_QUERY, directorId)
                .orElseThrow(() -> new DirectorNotFoundException("Ошибка при поиске режиссера c id" + directorId + "."));
    }

    @Override
    public Director create(Director director) {
        log.debug("Добавляем данные нового режиссера в бд.");
        Long key = insert(CREATE_QUERY, director.getName());
        return getDirectorById(key);
    }

    @Override
    public Director update(Director director) {
        log.debug("Обновляем данные режиссера.");
        // проверяем есть ли такой режиссер в бд, прежде чем изменять данные
        getDirectorById(director.getId());
        update(UPDATE_QUERY, director.getName(), director.getId());
        return getDirectorById(director.getId());
    }

    @Override
    public void delete(Long directorId) {
        log.debug("Удаляем режиссера из бд по его id.");
        update(DELETE_QUERY, directorId);
    }

    @Override
    public void createFilmAndDirConn(Long filmId, Set<Director> directors) {
        log.debug("Объединяем фильм и его режиссера по их id.");
        for (Director director : directors) {
            try {
                getDirectorById(director.getId());
                insert(CREATE_FILM_AND_DIR_CONN, filmId, director.getId());
            } catch (InvalidDataAccessApiUsageException ignored) {
            }
        }
    }
}
