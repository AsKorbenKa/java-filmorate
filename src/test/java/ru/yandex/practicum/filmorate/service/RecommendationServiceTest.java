package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan("ru.yandex.practicum.filmorate")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RecommendationServiceTest {
    FilmDbStorage filmDbStorage;
    RecommendationService recommendationService;
    FilmService filmService;

    @Sql(value = {"/delete_likes.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void getRecommendationTest() {
        filmDbStorage.addLike(1L, 1L);
        filmDbStorage.addLike(2L, 1L);
        filmDbStorage.addLike(3L, 1L);
        filmDbStorage.addLike(2L, 2L);
        filmDbStorage.addLike(3L, 2L);
        filmDbStorage.addLike(4L, 2L);
        filmDbStorage.addLike(1L, 3L);
        filmDbStorage.addLike(4L, 3L);
        filmDbStorage.addLike(5L, 3L);
        filmDbStorage.addLike(1L, 4L);
        filmDbStorage.addLike(2L, 4L);
        filmDbStorage.addLike(5L, 4L);
        filmDbStorage.addLike(1L, 5L);
        filmDbStorage.addLike(4L, 5L);
        filmDbStorage.addLike(5L, 5L);

        List<FilmDto> recommendedFilms = recommendationService.getRecommendedFilms(1L);
        FilmDto film4 = filmService.getFilmById(4L);
        FilmDto film5 = filmService.getFilmById(5L);
        assertTrue(recommendedFilms.contains(film4));
        assertTrue(recommendedFilms.contains(film5));
    }
}
