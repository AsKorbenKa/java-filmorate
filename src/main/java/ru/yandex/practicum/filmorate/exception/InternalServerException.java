package ru.yandex.practicum.filmorate.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(final String message) {
        super(message);
    }
}
