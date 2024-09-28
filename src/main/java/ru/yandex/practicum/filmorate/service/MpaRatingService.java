package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewMpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.rating.MpaRatingStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MpaRatingService {
    MpaRatingStorage mpaRatingStorage;

    public Collection<MpaRating> getAllMpaRatings() {
        return mpaRatingStorage.getAllMpaRatings();
    }

    public MpaRating getMpaRatingById(Long ratingId) {
        return mpaRatingStorage.getRatingById(ratingId);
    }

    public MpaRating create(NewMpaRatingDto mpaRatingDto) {
        return mpaRatingStorage.create(mpaRatingDto);
    }

    public void delete(Long ratingId) {
        mpaRatingStorage.delete(ratingId);
    }
}
