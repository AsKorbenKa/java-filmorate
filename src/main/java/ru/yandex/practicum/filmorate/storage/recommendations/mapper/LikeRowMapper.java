package ru.yandex.practicum.filmorate.storage.recommendations.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class LikeRowMapper implements RowMapper<Map<Long, Set<Long>>> {

    @Override
    public Map<Long, Set<Long>> mapRow(ResultSet rs, int rowNum) throws SQLException {
        HashMap<Long, Set<Long>> likes = new HashMap<>();
        do {
            likes.computeIfAbsent(rs.getLong("USER_ID"), value -> new HashSet<>()).add(rs.getLong("LIKED_FILM"));
        } while (rs.next());
        return likes;
    }
}
