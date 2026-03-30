package ru.yandex.practicum.exception.model;

public class FallbackException extends RuntimeException {
    public FallbackException(String message) {
        super(message);
    }
}
