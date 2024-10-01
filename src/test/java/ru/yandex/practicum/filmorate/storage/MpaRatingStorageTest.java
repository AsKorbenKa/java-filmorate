package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.dto.NewMpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.rating.MpaRatingStorage;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan("ru.yandex.practicum.filmorate")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaRatingStorageTest {
    MpaRatingStorage mpaRatingStorage;

    @Test
    public void testGetMpaRating() {
        assertThat(mpaRatingStorage.getAllMpaRatings()).isNotEmpty()
                .hasSize(5)
                .filteredOn("name", "R")
                .isNotEmpty()
                .hasExactlyElementsOfTypes(MpaRating.class);
    }

    @Test
    public void testGetMpaRatingById() {
        assertThat(mpaRatingStorage.getRatingById(1L))
                .isInstanceOf(MpaRating.class)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    public void testCreateMpaRating() {
        NewMpaRatingDto newMpaRating = new NewMpaRatingDto();
        newMpaRating.setName("GG");

        assertThat(mpaRatingStorage.create(newMpaRating))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 6L)
                .hasFieldOrPropertyWithValue("name", "GG");
    }

    @Test
    public void testDeleteMpaRating() {
        assertThat(mpaRatingStorage.getAllMpaRatings()).isNotEmpty()
                .hasSize(5) // 1L, 2L, 3L, 4L, 5L
                .filteredOn("name", "R")
                .isNotEmpty()
                .hasExactlyElementsOfTypes(MpaRating.class);

        mpaRatingStorage.delete(5L);

        assertThat(mpaRatingStorage.getAllMpaRatings()).isNotEmpty()
                .hasSize(4) // 1L, 2L, 3L, 4L
                .filteredOn("name", "R")
                .isNotEmpty()
                .hasExactlyElementsOfTypes(MpaRating.class);
    }
}
