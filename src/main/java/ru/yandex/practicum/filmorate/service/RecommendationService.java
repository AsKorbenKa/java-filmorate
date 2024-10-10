package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.recommendations.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.rating.MpaRatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final MpaRatingStorage mpaRatingStorage;
    private final RecommendationStorage recommendationStorage;
    private final Map<Long, Set<Long>> likes = recommendationStorage.getLikes();


    public List<Long> recommendFilms(Long userId) {
        Set<Long> recommendedFilms = new HashSet<>();
        if (!likes.containsKey(userId)) {
            return Collections.emptyList();
        }

        Set<Long> similarUsers = findSimilarUsers(userId);
        Set<Long> userLikedFilms = likes.get(userId);
        for (Long similarUserId : similarUsers) {
            for (Long filmId : likes.get(similarUserId)) {
                if (!userLikedFilms.contains(filmId)) {
                    recommendedFilms.add(filmId);
                }
            }
        }
        return new ArrayList<>(recommendedFilms);
    }

    private Set<Long> findSimilarUsers(Long userId) {
        Set<Long> similarUsers = new HashSet<>();
        int maxIntersection = 0;

        for (Map.Entry<Long, Set<Long>> entry : likes.entrySet()) {
            if (entry.getKey() != userId) {
                int intersectionSize = getIntersectionSize(likes.get(userId), entry.getValue());
                if (intersectionSize > maxIntersection) {
                    maxIntersection = intersectionSize;
                    similarUsers.clear();
                    similarUsers.add(entry.getKey());
                } else if (intersectionSize == maxIntersection) {
                    similarUsers.add(entry.getKey());
                }
            }
        }

        return similarUsers;
    }

    private int getIntersectionSize(Set<Long> set1, Set<Long> set2) {
        return (int) set1.stream()
                .filter(set2::contains)
                .count();
    }
}
