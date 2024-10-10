package ru.yandex.practicum.filmorate.storage.recommendations;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.recommendations.mapper.GetLikesRowMapper;
import ru.yandex.practicum.filmorate.storage.recommendations.mapper.LikeRowMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RecommendationStorage {
    JdbcTemplate jdbc;
    LikeRowMapper mapper;
    GetLikesRowMapper getLikesMapper;
    static String FIND_LIKES_QUERY = "SELECT USER_ID, FILM_ID AS liked_film FROM FILM_LIKES fl ORDER BY USER_ID;";
    static String FIND_LIKES_BY_ID_QUERY = "SELECT FILM_ID FROM FILM_LIKES fl WHERE fl.user_id = ?;";

    public Map<Long, Set<Long>> getLikes() {
        return jdbc.query(FIND_LIKES_QUERY, mapper).getFirst();
    }

    public Set<Long> getLikesById(Long userId) {
        List<Set<Long>> likes = jdbc.query(FIND_LIKES_BY_ID_QUERY, getLikesMapper, userId);
        if (likes.isEmpty()) {
            return new HashSet<>();
        }
        return likes.getFirst();
    }
}
