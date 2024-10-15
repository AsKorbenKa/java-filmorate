package ru.yandex.practicum.filmorate.storage.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Opertion;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;

@Repository
@Slf4j
public class FeedDbStorage extends BaseStorage<Feed> implements FeedStorage {

    public FeedDbStorage(JdbcTemplate jdbc, RowMapper<Feed> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Feed> findFeedByUserId(Long userId) {
        log.info("Получение листа событий пользователя с id {} ", userId);
        String sqlQuery = "SELECT * FROM feeds WHERE user_id = ?";
        return findMany(sqlQuery, userId);
    }

    @Override
    public void addFeed(Long entityId, Long userId, EventType eventType, Opertion opertion) {
        log.info("Добавление в лист событий записи ");
        String sqlQuery = "INSERT INTO feeds (user_id, entity_id, event_type, operation) " +
                "VALUES (?, ?, ?, ?)";
        update(sqlQuery, userId, entityId, eventType.toString(), opertion.toString());

    }
}
