package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.dto.CreateFilmDto;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewDto;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan("ru.yandex.practicum.filmorate")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewStorageTest {
    ReviewDbStorage reviewStorage;

    @Test
    public void testGetReview() {
        assertThat(reviewStorage.getReview(1L))
                .isInstanceOf(Review.class)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("content", "This film is really bad")
                .hasFieldOrPropertyWithValue("isPositive", false)
                .hasFieldOrPropertyWithValue("userId", 1L)
                .hasFieldOrPropertyWithValue("filmId", 1L)
                .hasFieldOrPropertyWithValue("useful", 0);
    }

    @Test
    public void testCreateReview() {
        ReviewDto newReview = new ReviewDto();
        newReview.setContent("This film is great");
        newReview.setIsPositive(false);
        newReview.setFilmId(2L);
        newReview.setUserId(2L);
        assertThat(reviewStorage.create(newReview))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 4L);
    }

    @Test
    public void testUpdateReview() {
        UpdateReviewDto newReview = new UpdateReviewDto();
        newReview.setReviewId(3L);
        newReview.setContent("I don't like this film");
        newReview.setIsPositive(false);
        assertThat(reviewStorage.update(newReview))
                .isInstanceOf(Review.class)
                .hasFieldOrPropertyWithValue("content", "I don't like this film")
                .hasFieldOrPropertyWithValue("isPositive", false);
    }

    @Test
    public void testGetAllReviewsWithId() {
        assertEquals(2, reviewStorage.getAllReviews(1L, 10).size());
    }

    @Test
    public void testGetAllReviewsWithoutId() {
        assertEquals(3, reviewStorage.getAllReviews(null, 10).size());
    }

    @Test
    public void testDeleteReview() {
        reviewStorage.delete(3L);
        assertThrows(ReviewNotFoundException.class, () -> reviewStorage.getReview(3L));
    }

    @Test
    public void testLike() {
        reviewStorage.like(1L, 1L);
        assertEquals(1, reviewStorage.getReview(1L).getUseful());
    }

    @Test
    public void testDislike() {
        reviewStorage.dislike(1L, 1L);
        assertEquals(-1, reviewStorage.getReview(1L).getUseful());
    }

    @Test
    public void testDeleteLike() {
        reviewStorage.like(1L, 1L);
        reviewStorage.deleteLike(1L, 1L);
        assertEquals(0, reviewStorage.getReview(1L).getUseful());
    }

    @Test
    public void testDeleteDislike() {
        reviewStorage.dislike(1L, 1L);
        reviewStorage.deleteDislike(1L, 1L);
        assertEquals(0, reviewStorage.getReview(1L).getUseful());
    }

    @Test
    public void testGetAllReviewsGivesCorrectOrder() {
        reviewStorage.like(3L, 1L);
        reviewStorage.like(3L, 2L);
        reviewStorage.dislike(2L, 2L);
        List<Review> reviews = reviewStorage.getAllReviews(null, 10);
        assertEquals(3, reviews.size());
        assertEquals(3L, reviews.get(0).getId());
        assertEquals(1L, reviews.get(1).getId());
        assertEquals(2L, reviews.get(2).getId());
    }
}
