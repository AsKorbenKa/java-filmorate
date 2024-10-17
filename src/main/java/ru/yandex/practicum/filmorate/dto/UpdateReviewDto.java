package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateReviewDto {
    @NotNull
    @Positive
    Long reviewId;
    String content;
    Boolean isPositive;
    Long userId;
    Long filmId;
    Integer useful;
}
