package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewMpaRatingDto {
    @NotBlank(message = "Название рейтинга не может быть пустым")
    String name;
}
