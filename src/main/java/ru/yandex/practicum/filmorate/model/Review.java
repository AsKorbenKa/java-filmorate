package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    Long id;
    @NotNull
    @NotBlank(message = "Текст отзыва не может быть пустым")
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    @Positive
    Long userId;
    @NotNull
    @Positive
    Long filmId;
    Integer useful = 0;
}
