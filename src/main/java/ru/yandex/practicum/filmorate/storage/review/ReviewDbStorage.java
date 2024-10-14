package ru.yandex.practicum.filmorate.storage.review;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewDto;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;
import java.util.List;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewDbStorage extends BaseStorage<Review>  {

    static Logger log = LoggerFactory.getLogger(UserDbStorage.class.getName());
    static String CREATE_REVIEW_QUERY = "INSERT INTO reviews (content, is_positive, user_id, film_id) " +
            "VALUES (?, ?, ?, ?)";
    static String UPDATE_REVIEW_QUERY = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
    static String GET_REVIEW_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    static String DELETE_REVIEW_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    static String GET_ALL_REVIEWS_QUERY = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
    static String GET_ALL_REVIEWS_BY_ID_QUERY = "SELECT * FROM reviews WHERE film_id = ? " +
            "ORDER BY useful DESC LIMIT ?";
    static String INCREASE_REVIEW_RATING_QUERY = "UPDATE reviews SET useful = useful + 1 " +
            "WHERE review_id = ?";
    static String DECREASE_REVIEW_RATING_QUERY = "UPDATE reviews SET useful = useful - 1 " +
            "WHERE review_id = ?";
    static String GET_LIKE_QUERY = "SELECT * FROM reviews_likes WHERE review_id = ? " +
            "AND user_id = ? AND is_like = ?";
    static String ADD_LIKE_QUERY = "INSERT INTO reviews_likes" +
            " (review_id, user_id, is_like) VALUES (?, ?, ?)";
    static String DELETE_LIKE_QUERY = "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ?";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    public Review create(ReviewDto review) {
        log.debug("Добавляем новый отзыв в базу данных.");
        Long key = insert(CREATE_REVIEW_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId());
        return getReview(key);
    }

    public Review getReview(Long reviewId) {
        log.debug("Получаем данные отзыва по его id.");
        return findOne(GET_REVIEW_QUERY, reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв с id " + reviewId + " не найден."));
    }

    public Review update(UpdateReviewDto newReview) {
        log.debug("Обновляем данные отзыва в базе данных.");
        Review oldReview = getReview(newReview.getReviewId());
        update(UPDATE_REVIEW_QUERY,
                newReview.getContent() == null || newReview.getContent().isBlank() ?
                        oldReview.getContent() : newReview.getContent(),
                newReview.getIsPositive() == null ? oldReview.getIsPositive() : newReview.getIsPositive(),
                newReview.getReviewId());
        return getReview(newReview.getReviewId());
    }

    public void delete(Long reviewId) {
        log.debug("Удаляем отзыв в базе данных.");
        update(DELETE_REVIEW_QUERY, reviewId);
    }

    public List<Review> getAllReviews(Long filmId, int count) {
        log.debug("Получаем все отзывы.");
        Collection<Review> coll;
        if (filmId == null) {
            coll = findMany(GET_ALL_REVIEWS_QUERY, count);
        } else {
            coll = findMany(GET_ALL_REVIEWS_BY_ID_QUERY, filmId, count);
        }
        return coll.stream().toList();
    }

    public void like(Long reviewId, Long userId) {
        if (!checkIfLikeExists(reviewId, userId, true)) {
            log.debug("Добавляем лайк отзыву в базе данных.");
            deleteDislike(reviewId, userId);
            update(ADD_LIKE_QUERY,
                    reviewId,
                    userId,
                    true);
            update(INCREASE_REVIEW_RATING_QUERY, reviewId);
        }
    }

    public void dislike(Long reviewId, Long userId) {
        if (!checkIfLikeExists(reviewId, userId, false)) {
            log.debug("Добавляем дизлайк отзыву в базе данных.");
            deleteLike(reviewId, userId);
            update(ADD_LIKE_QUERY,
                    reviewId,
                    userId,
                    false);
            update(DECREASE_REVIEW_RATING_QUERY, reviewId);
        }
    }

    public void deleteLike(Long reviewId, Long userId) {
        if (checkIfLikeExists(reviewId, userId, true)) {
            log.debug("Удаляем лайк отзыва в базе данных.");
            update(DELETE_LIKE_QUERY, reviewId, userId);
            update(DECREASE_REVIEW_RATING_QUERY, reviewId);
        }
    }

    public void deleteDislike(Long reviewId, Long userId) {
        if (checkIfLikeExists(reviewId, userId, false)) {
            log.debug("Удаляем дизлайк отзыва в базе данных.");
            update(DELETE_LIKE_QUERY, reviewId, userId);
            update(INCREASE_REVIEW_RATING_QUERY, reviewId);
        }
    }

    private boolean checkIfLikeExists(Long reviewId, Long userId, boolean isLike) {
        if (isLike) {
            return !jdbc.queryForList(GET_LIKE_QUERY, reviewId, userId, true).isEmpty();
        } else {
            return !jdbc.queryForList(GET_LIKE_QUERY, reviewId, userId, false).isEmpty();
        }
    }
}
