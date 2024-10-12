package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    FilmStorage filmStorage;
    UserStorage userStorage;
    ReviewDbStorage reviewStorage;

    public ReviewDto getReview(Long id) {
        return ReviewMapper.reviewDtoMapper(reviewStorage.getReview(id));
    }

    public ReviewDto deleteReview(Long id) {
        Review review = reviewStorage.getReview(id);
        reviewStorage.delete(id);
        return ReviewMapper.reviewDtoMapper(review);
    }

    public ReviewDto createReview(ReviewDto reviewDto) {
        return null;
    }

    public ReviewDto updateReview(ReviewDto newReview) {
        return null;
    }

    public Collection<ReviewDto> getAllReviews(Long filmId, int count) {
        Collection<Review> coll;
        if (filmId == null) {
            coll = reviewStorage.getAllReviews(count);
        } else {
            coll = reviewStorage.getAllReviewsById(filmId, count);
        }
        return coll
                .stream()
                .map(ReviewMapper::reviewDtoMapper)
                .toList();
    }

    public void like(Long reviewId, Long userId) {
    }

    public void dislike(Long reviewId, Long userId) {
    }

    public void deleteLike(Long reviewId, Long userId) {
    }

    public void deleteDislike(Long reviewId, Long userId) {
    }

    private void checkIfUserAndReviewExist(Long reviewId, Long userId) {
        reviewStorage.getReview(reviewId);
        userStorage.getUserById(userId);
    }
}
