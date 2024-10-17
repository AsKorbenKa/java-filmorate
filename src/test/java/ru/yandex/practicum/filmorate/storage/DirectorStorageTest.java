package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan("ru.yandex.practicum.filmorate")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorStorageTest {
    DirectorStorage directorStorage;

    @Test
    public void testFindAllDirectors() {
        assertThat(directorStorage.findAllDirectors())
                .isNotEmpty()
                .hasSize(5)
                .filteredOn("name", "Кристофер Нолан")
                .isNotEmpty()
                .hasExactlyElementsOfTypes(Director.class);
    }

    @Test
    public void testGetDirectorOfTheFilm() {
        Long filmId = 1L;

        assertThat(directorStorage.getDirectorOfTheFilm(filmId))
                .isNotEmpty()
                .isInstanceOf(HashSet.class)
                .first()
                .extracting(Director::getName)
                .isEqualTo("Тим Бёртон");
    }

    @Test
    public void testGetDirectorById() {
        Long directorId = 2L;

        assertThat(directorStorage.getDirectorById(directorId))
                .isInstanceOf(Director.class)
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", "Стивен Спилберг");
    }

    @Test
    public void testCreateDirector() {
        Director director = new Director();
        director.setName("Мартин Скорсезе");

        assertThat(directorStorage.create(director))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 6L);
    }

    @Test
    public void testUpdateDirector() {
        Director director = new Director();
        director.setId(4L);
        director.setName("Мартин Скорсезе");

        assertThat(directorStorage.update(director))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 4L)
                .hasFieldOrPropertyWithValue("name", "Мартин Скорсезе");
    }

    @Test void testDeleteDirector() {
        Long directorId = 4L;

        assertThat(directorStorage.findAllDirectors())
                .isNotEmpty()
                .hasSize(5);

        directorStorage.delete(directorId);

        assertThat(directorStorage.findAllDirectors())
                .isNotEmpty()
                .hasSize(4);
    }
}
