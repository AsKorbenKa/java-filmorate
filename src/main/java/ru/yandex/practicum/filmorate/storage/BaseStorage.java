package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class BaseStorage<T> {
    static Logger log = LoggerFactory.getLogger(FilmDbStorage.class.getName());
    JdbcTemplate jdbc;
    RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected Collection<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    protected void update(String query, Object... params) {
        try {
            jdbc.update(query, params);
        } catch (DataAccessException e) {
            log.error("Что-то пошло не так при выполнении инструкции SQL. Не удалось обновить данные.", e);
        }
    }

    protected long insert(String query, Object... params) {
        Long id;
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                for (int idx = 0; idx < params.length; idx++) {
                    ps.setObject(idx + 1, params[idx]);
                }
                return ps; }, keyHolder);
        } catch (DataAccessException e) {
            log.error("Что-то пошло не так при выполнении инструкции SQL. " +
                    "Не удалось добавить новые данные в базу данных.", e);
        }

        id = keyHolder.getKeyAs(Long.class);

        // Возвращаем id нового пользователя
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
