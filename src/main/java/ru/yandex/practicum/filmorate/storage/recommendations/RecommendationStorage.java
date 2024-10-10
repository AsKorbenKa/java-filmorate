package ru.yandex.practicum.filmorate.storage.recommendations;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.recommendations.mapper.LikeRowMapper;

import java.util.Map;
import java.util.Set;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RecommendationStorage {
    private final JdbcTemplate jdbc;
    private final LikeRowMapper mapper;
    static final String FIND_LIKES_QUERY = "SELECT USER_ID, FILM_ID AS liked_film FROM FILM_LIKES fl ORDER BY USER_ID;";

    public Map<Long, Set<Long>> getLikes() {
        return jdbc.query(FIND_LIKES_QUERY, mapper).getFirst();
    }

}
