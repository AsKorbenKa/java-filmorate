package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {

    ReviewService reviewService;

    @GetMapping("/{id}")
    public ReviewDto getReview(@PathVariable("id") Long id) {
        return reviewService.getReview(id);
    }

    @DeleteMapping("/{id}")
    public ReviewDto deleteReview(@PathVariable("id") Long id) {
        return reviewService.deleteReview(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto createReview(@Valid @RequestBody ReviewDto reviewDto) {
        return reviewService.createReview(reviewDto);
    }

    @PutMapping()
    public ReviewDto updateReview(@Valid @RequestBody ReviewDto newReview) {
        return reviewService.updateReview(newReview);
    }

    @GetMapping("/popular")
    public Collection<ReviewDto> getAllReviews(@RequestParam(required = false) Long filmId,
                                               @RequestParam(defaultValue = "10") int count) {
        return reviewService.getAllReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        reviewService.like(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        reviewService.dislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        reviewService.deleteDislike(id, userId);
    }
}
