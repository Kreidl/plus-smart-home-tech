package ru.yandex.practicum.exception.model;

public class NoPaymentFoundException extends RuntimeException {
  public NoPaymentFoundException(String message) {
    super(message);
  }
}
