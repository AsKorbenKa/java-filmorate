package ru.yandex.practicum.filmorate.storage.recommendations.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class GetLikesRowMapper implements RowMapper<Set<Long>> {
    @Override
    public Set<Long> mapRow(ResultSet rs, int rowNum) throws SQLException {
        Set<Long> likes = new HashSet<>();
        do {
            likes.add(rs.getLong("FILM_ID"));
        } while (rs.next());
        return likes;
    }
}
