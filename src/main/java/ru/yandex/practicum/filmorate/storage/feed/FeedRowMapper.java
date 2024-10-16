package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedRowMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
        Feed feed = new Feed();
        feed.setEventId(rs.getLong("event_id"));
        feed.setUserId(rs.getLong("user_id"));
        feed.setEntityId(rs.getLong("entity_id"));
        feed.setEventType(EventType.valueOf(rs.getString("event_type")));
        feed.setOperation(Operation.valueOf(rs.getString("operation")));
        feed.setTimestamp(rs.getTimestamp("timestamp_feed").getTime());
        return feed;
    }
}
