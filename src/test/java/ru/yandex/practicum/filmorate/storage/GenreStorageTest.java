package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.dto.NewGenreDto;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan("ru.yandex.practicum.filmorate")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreStorageTest {
    GenreStorage genreStorage;

    @Test
    public void testGetAllGenres() {
        assertThat(genreStorage.getGenres()).isNotEmpty()
                .hasSize(6)
                .filteredOn("name", "Комедия")
                .isNotEmpty()
                .hasExactlyElementsOfTypes(Genre.class);
    }

    @Test
    public void testGetGenreById() {
        assertThat(genreStorage.getGenreById(3L))
                .isInstanceOf(Genre.class)
                .hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("name", "Мультфильм");
    }

    @Test
    public void testCreateGenre() {
        NewGenreDto newGenre = new NewGenreDto();
        newGenre.setName("Фантастика");

        assertThat(genreStorage.create(newGenre))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 7L)
                .hasFieldOrPropertyWithValue("name", "Фантастика");
    }

    @Test
    public void testDeleteGenre() {
        genreStorage.delete(6L);

        assertThat(genreStorage.getGenres()).isNotEmpty()
                .hasSize(5)
                .filteredOn("name", "Комедия")
                .isNotEmpty()
                .hasExactlyElementsOfTypes(Genre.class);
    }
}
