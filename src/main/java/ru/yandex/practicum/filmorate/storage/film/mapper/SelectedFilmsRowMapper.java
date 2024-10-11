package ru.yandex.practicum.filmorate.storage.film.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class SelectedFilmsRowMapper implements RowMapper<Map<Long, Film>> {

    @Override
    public Map<Long, Film> mapRow(ResultSet rs, int rowNum) throws SQLException {
        HashMap<Long, Film> films = new HashMap<>();
        do {
            Film newFilm = films.computeIfAbsent(rs.getLong("FILM_ID"), film -> new Film());
            if (newFilm.getId() == null) {
                newFilm.setId(rs.getLong("FILM_ID"));
                newFilm.setName(rs.getString("NAME"));
                newFilm.setDescription(rs.getString("DESCRIPTION"));
                newFilm.setReleaseDate(rs.getDate("RELEASEDATE").toLocalDate());
                newFilm.setDuration(rs.getLong("DURATION"));
                MpaRating mpaRating = new MpaRating();
                mpaRating.setId(rs.getLong("RATING_ID"));
                mpaRating.setName(rs.getString("RATING"));
                newFilm.setMpa(mpaRating);
            }
            Genre genre = new Genre();
            genre.setId(rs.getLong("GENRE_ID"));
            genre.setName(rs.getString("GENRE"));
            if (newFilm.getGenres() == null) {
                newFilm.setGenres(new HashSet<>());
            }
            if (genre.getId() != 0) {
                newFilm.getGenres().add(genre);
            }
            Director director = new Director();
            director.setId(rs.getLong("DIRECTOR_ID"));
            director.setName(rs.getString("DIRECTOR"));
            if (newFilm.getDirectors() == null) {
                newFilm.setDirectors(new HashSet<>());
            }
            if (director.getId() != 0) {
                newFilm.getDirectors().add(director);
            }
            films.put(newFilm.getId(), newFilm);
        } while (rs.next());
        return films;
    }
}
