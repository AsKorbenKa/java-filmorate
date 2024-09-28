package ru.yandex.practicum.filmorate.exception;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException(final String message) {
        super(message);
    }
}
