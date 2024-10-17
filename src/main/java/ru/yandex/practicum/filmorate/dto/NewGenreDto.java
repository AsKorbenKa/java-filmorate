package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewGenreDto {
    @NotBlank(message = "Название жанра не может быть пустым")
    String name;
}
