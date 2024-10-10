package ru.yandex.practicum.filmorate.storage.film.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SelectedFilmsRowMapper implements RowMapper<Map<Long, Film>> {

    @Override
    public Map<Long, Film> mapRow(ResultSet rs, int rowNum) throws SQLException {
        HashMap<Long, Film> films = new HashMap<>();
        do {
            Film newFilm = films.computeIfAbsent(rs.getLong("FILM_ID"), film -> new Film());
            newFilm.setId();
            newFilm.setName();
            newFilm.setDescription();
            newFilm.setReleaseDate();
            newFilm.setDuration();
            newFilm.

        } while (rs.next());


        return ;
    }
}
