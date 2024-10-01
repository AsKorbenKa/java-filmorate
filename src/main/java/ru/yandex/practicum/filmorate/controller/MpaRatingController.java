package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewMpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MpaRatingController {
    MpaRatingService mpaRatingService;

    @GetMapping
    public Collection<MpaRating> getAllMpaRatings() {
        return mpaRatingService.getAllMpaRatings();
    }

    @GetMapping("/{id}")
    public MpaRating getMpaRatingById(@PathVariable("id") Long ratingId) {
        return mpaRatingService.getMpaRatingById(ratingId);
    }

    @PostMapping
    public MpaRating create(@Valid @RequestBody NewMpaRatingDto mpaRatingDto) {
        return mpaRatingService.create(mpaRatingDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long ratingId) {
        mpaRatingService.delete(ratingId);
    }
}
