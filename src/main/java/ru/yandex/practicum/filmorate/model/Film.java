package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.DateAfterStandart;

import java.time.LocalDate;

@Data
public class Film {
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Длина описания фильма не может быть более 200 символов")
    private String description;
    @DateAfterStandart(message = "Дата релиза фильма не может быть раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не может быть меньше 0")
    private Long duration;
}
