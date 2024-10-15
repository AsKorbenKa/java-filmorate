package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Opertion;

@Data
public class Feed {

    private Long eventId;

    @NotNull(message = "Не указан id автора события")
    private Long userId;

    @NotNull(message = "Не указан id события")
    private Long entityId;

    @NotEmpty(message = "Не указан тип события")
    private EventType eventType;

    @NotEmpty(message = "Не указана операция")
    private Opertion operation;

    private Long timestamp;
}
