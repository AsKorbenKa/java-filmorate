package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.dto.NewMpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaRatingStorage {
    Collection<MpaRating> getAllMpaRatings();

    MpaRating getRatingById(Long ratingId);

    MpaRating getFilmMpaRating(Long filmId);

    MpaRating create(NewMpaRatingDto newMpaRatingDto);

    void delete(Long ratingId);

    void createMpaAndFilmConn(Long filmId, MpaRating mpa);
}
