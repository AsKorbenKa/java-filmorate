package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private Film film;
    private static Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validateFilmName() {
        film = new Film();
        film.setId(1L);
        film.setName(" ");
        film.setDescription("Триллер из вселенной Гарри Поттера");
        film.setReleaseDate(LocalDate.of(2014,5,23));
        film.setDuration(120L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Название фильма не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void validateFildDescription() {
        film = new Film();
        film.setId(1L);
        film.setName("Побег из Азкабана");
        film.setDescription("Триллер из вселенной Гарри Поттера. В этом фильме происходит невесть что. " +
                "Даже самым ярым фанатам этой вселенной станет плохо от происходящего на экране!!! " +
                "Увы, Добби переступил черту закона и теперь является беглым преступником. " +
                "Макгонагал теперь крышует таверну \"Три метлы\" и жестоко наказывает неугодных.");
        film.setReleaseDate(LocalDate.of(2014,5,23));
        film.setDuration(120L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Длина описания фильма не может быть более 200 символов",
                violations.iterator().next().getMessage());
    }

    @Test
    void validateFilmReleaseDate() {
        film = new Film();
        film.setId(1L);
        film.setName("Побег из Азкабана");
        film.setDescription("Триллер из вселенной Гарри Поттера");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Дата релиза фильма не может быть раньше 28 декабря 1895 года", violations.iterator().next().getMessage());
    }

    @Test
    void validateFilmDuration() {
        film = new Film();
        film.setId(1L);
        film.setName("Побег из Азкабана");
        film.setDescription("Триллер из вселенной Гарри Поттера");
        film.setReleaseDate(LocalDate.of(2014,5,23));
        film.setDuration(-120L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Продолжительность фильма не может быть меньше 0",
                violations.iterator().next().getMessage());
    }
}