package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewMpaRatingDto {
    @NotBlank(message = "Название рейтинга не может быть пустым")
    String name;
}
