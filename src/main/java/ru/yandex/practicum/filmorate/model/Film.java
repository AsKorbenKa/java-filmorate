package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotation.DateAfterStandard;

import java.time.LocalDate;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    String name;
    @Size(max = 200, message = "Длина описания фильма не может быть более 200 символов")
    String description;
    @DateAfterStandard(message = "Дата релиза фильма не может быть раньше 28 декабря 1895 года")
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не может быть меньше 0")
    Long duration;
    MpaRating mpa;
    Set<Genre> genres;
    Set<Director> directors;
}
