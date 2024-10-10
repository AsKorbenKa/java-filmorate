package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {
    public static FilmDto filmDtoMapper(Film film, MpaRating mpaRating, Set<Genre> genres, Set<Director> director) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setDuration(film.getDuration());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setMpa(mpaRating);
        filmDto.setGenres(genres);
        filmDto.setDirectors(director);
        return filmDto;
    }
}
