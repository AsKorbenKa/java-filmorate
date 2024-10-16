package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewDto {
    Long reviewId;
    @NotNull
    @NotBlank(message = "Текст отзыва не может быть пустым")
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    Long userId;
    @NotNull
    Long filmId;
    Integer useful = 0;
}
