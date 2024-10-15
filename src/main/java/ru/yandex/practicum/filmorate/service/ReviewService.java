package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewDto;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Opertion;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    FilmStorage filmStorage;
    UserStorage userStorage;
    ReviewDbStorage reviewStorage;
    FeedStorage feedStorage;

    public ReviewDto getReview(Long id) {
        return ReviewMapper.reviewDtoMapper(reviewStorage.getReview(id));
    }

    public ReviewDto deleteReview(Long id) {
        Review review = reviewStorage.getReview(id);
        reviewStorage.delete(id);
        //добавление в ленту событий
        feedStorage.addFeed(review.getId(), review.getUserId(), EventType.REVIEW, Opertion.REMOVE);
        return ReviewMapper.reviewDtoMapper(review);
    }

    public ReviewDto createReview(ReviewDto reviewDto) {
        checkIfUserAndFilmExist(reviewDto.getUserId(), reviewDto.getFilmId());
        Review review = reviewStorage.create(reviewDto);
        //добавление в ленту событий
        feedStorage.addFeed(review.getId(), review.getUserId(), EventType.REVIEW, Opertion.ADD);
        return ReviewMapper.reviewDtoMapper(review);
    }

    public ReviewDto updateReview(UpdateReviewDto newReview) {
        checkIfUserAndFilmExist(newReview.getUserId(), newReview.getFilmId());
        Review review = reviewStorage.update(newReview);
        //добавление в ленту событий
        feedStorage.addFeed(review.getId(), review.getUserId(), EventType.REVIEW, Opertion.UPDATE);
        return ReviewMapper.reviewDtoMapper(review);
    }

    public List<ReviewDto> getAllReviews(Long filmId, int count) {
        return reviewStorage.getAllReviews(filmId, count)
                .stream()
                .map(ReviewMapper::reviewDtoMapper)
                .toList();
    }

    public void like(Long reviewId, Long userId) {
        checkIfUserAndReviewExist(reviewId, userId);
        reviewStorage.like(reviewId, userId);
    }

    public void dislike(Long reviewId, Long userId) {
        checkIfUserAndReviewExist(reviewId, userId);
        reviewStorage.dislike(reviewId, userId);
    }

    public void deleteLike(Long reviewId, Long userId) {
        checkIfUserAndReviewExist(reviewId, userId);
        reviewStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        checkIfUserAndReviewExist(reviewId, userId);
        reviewStorage.deleteDislike(reviewId, userId);
    }

    private void checkIfUserAndReviewExist(Long reviewId, Long userId) {
        reviewStorage.getReview(reviewId);
        userStorage.getUserById(userId);
    }

    private void checkIfUserAndFilmExist(Long userId, Long filmId) {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
    }
}
