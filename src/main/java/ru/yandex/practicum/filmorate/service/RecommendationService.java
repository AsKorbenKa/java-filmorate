package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.recommendations.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecommendationService {
    FilmDbStorage filmStorage;
    UserDbStorage userStorage;
    RecommendationStorage recommendationStorage;

    public List<FilmDto> getRecommendedFilms(Long userId) {
        if (recommendationStorage.getLikesById(userId) == null || recommendationStorage.getLikesById(userId).isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> recommendedFilms = recommendedFilmsIds(userId);
        if (recommendedFilms.isEmpty()) {
            return new ArrayList<>();
        }
        return filmStorage.getFilmsById(recommendedFilms).values()
                .stream()
                .map(FilmMapper::fullFilmDtoMapper)
                .toList();
    }

    public List<Long> recommendedFilmsIds(Long userId) {
        Map<Long, Set<Long>> likes = recommendationStorage.getLikes();
        Set<Long> recommendedFilms = new HashSet<>();
        if (!likes.containsKey(userId)) {
            return Collections.emptyList();
        }

        Set<Long> similarUsers = findSimilarUsers(userId, likes);
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

    private Set<Long> findSimilarUsers(Long userId, Map<Long, Set<Long>> likes) {
        Set<Long> similarUsers = new HashSet<>();
        int maxIntersection = 0;

        for (Map.Entry<Long, Set<Long>> entry : likes.entrySet()) {
            if (entry.getKey() != userId) {
                int intersectionSize = getIntersectionSize(likes.get(userId), entry.getValue());
                if (intersectionSize > maxIntersection) {
                    maxIntersection = intersectionSize;
                    similarUsers.clear();
                    similarUsers.add(entry.getKey());
                } else if (intersectionSize == maxIntersection && intersectionSize != 0) {
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
