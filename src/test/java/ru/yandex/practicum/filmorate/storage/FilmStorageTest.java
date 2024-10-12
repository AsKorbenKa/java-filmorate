package ru.yandex.practicum.filmorate.storage;

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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.time.Year;
import java.util.Collection;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan("ru.yandex.practicum.filmorate")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class FilmStorageTest {
    FilmDbStorage filmStorage;

    @Test
    public void testCreateFilm() {
        FilmDto newFilm = new FilmDto();
        newFilm.setName("ВАЛЛИ");
        newFilm.setDescription("Покинуты всеми робот живет свой обычный день, собирая мусор, как вдруг...");
        newFilm.setReleaseDate(LocalDate.of(2012, 5, 12));
        newFilm.setDuration(100L);

        assertThat(filmStorage.create(newFilm))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 6L);
    }

    @Test
    public void testUpdateFilm() {
        FilmDto newFilm = new FilmDto();
        newFilm.setId(1L);
        newFilm.setName("ВАЛЛИ");
        newFilm.setDescription("Покинуты всеми робот живет свой обычный день, собирая мусор, как вдруг...");
        newFilm.setReleaseDate(LocalDate.of(2012, 5, 12));
        newFilm.setDuration(100L);

        assertThat(filmStorage.update(newFilm))
                .isInstanceOf(Film.class)
                .hasFieldOrPropertyWithValue("name", "ВАЛЛИ");
    }

    @Test
    public void testGetFilmById() {
        assertThat(filmStorage.getFilmById(1L))
                .isInstanceOf(Film.class)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Тень");
    }

    @Test
    public void testFindAll() {
        assertThat(filmStorage.findAll()).isNotEmpty()
                .hasSize(5)
                .filteredOn("name", "Тень")
                .isNotEmpty()
                .hasExactlyElementsOfTypes(Film.class);
    }

    @Test
    public void testFindPopular() {
        final int count = 4;
        Collection<Film> films = filmStorage.findMostPopularFilms(count);

        assertThat(films).isNotEmpty()
                .hasSize(count)
                .isInstanceOf(Collection.class)
                .first()
                .extracting(Film::getId)
                .isEqualTo(2L);
    }

    @Test
    public void testAddLike() {
        Long filmId = 3L;
        Long userId = 3L;

        assertThat(filmStorage.getFilmLikes(filmId))
                .isNotEmpty()
                .isInstanceOf(HashSet.class)
                .containsOnly(2L);

        filmStorage.addLike(filmId, userId);

        assertThat(filmStorage.getFilmLikes(filmId))
                .isNotEmpty()
                .isInstanceOf(HashSet.class)
                .hasSize(2)
                .containsOnly(2L, 3L);
    }

    @Test
    public void testRemoveLike() {
        Long filmId = 2L;
        Long userId = 3L;

        assertThat(filmStorage.getFilmLikes(filmId))
                .isNotEmpty()
                .isInstanceOf(HashSet.class)
                .hasSize(3)
                .containsOnly(1L, 2L, 3L);

        filmStorage.removeLike(filmId, userId);

        assertThat(filmStorage.getFilmLikes(filmId))
                .isNotEmpty()
                .isInstanceOf(HashSet.class)
                .hasSize(2)
                .containsOnly(1L, 2L);
    }

    @Test
    public void testFindSortedDirectorFilms() {
        Long directorId = 5L;
        String sortBy = "likes";
        String anotherSortBy = "year";

        assertThat(filmStorage.findSortedDirectorFilms(directorId, sortBy))
                .isNotEmpty()
                .isInstanceOf(Collection.class)
                .last()
                .extracting(Film::getId)
                .isEqualTo(1L);

        assertThat(filmStorage.findSortedDirectorFilms(directorId, anotherSortBy))
                .isNotEmpty()
                .isInstanceOf(Collection.class)
                .last()
                .extracting(Film::getId)
                .isEqualTo(2L);
    }

    @Test
    void testGetListFilmByUserId() {
        Collection<Film> films = filmStorage.getFilmLikedByUserId(1L);
        assertThat(films).isNotEmpty();
        assertThat(films).hasSize(3);
    }

    @Test
    void testGetEmptyListFilmByUserId() {
        Collection<Film> films = filmStorage.getFilmLikedByUserId(7L);
        assertThat(films).isEmpty();
        assertThat(films).hasSize(0);
    }

    @Test
    void searchFilmByTitleTest() {
        assertEquals(1, filmStorage.search("то", null).size());
    }

    @Test
    void searchFilmByDirector() {
        assertEquals(3, filmStorage.search(null, "то").size());
    }

    @Test
    void searchFilmByTitleAndDirector() {
        assertEquals(4, filmStorage.search("то", "то").size());
    }

    @Test
    void testFindSortedByConditions() {
        Long genreId = 5L;
        Year year = Year.of(1994);
        // получаем фильмы с определенным жанром
        assertThat(filmStorage.findSortedByConditions(genreId, Year.of(0)))
                .hasSize(4)
                .isInstanceOf(Collection.class)
                .first()
                .extracting(Film::getId)
                .isEqualTo(2L);

        // получаем фильмы с определенным годом выпуска
        assertThat(filmStorage.findSortedByConditions(0L, year))
                .hasSize(1)
                .isInstanceOf(Collection.class)
                .first()
                .extracting(Film::getId)
                .isEqualTo(1L);
    }
}
