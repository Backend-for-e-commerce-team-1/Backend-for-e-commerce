package ru.practicum.masters.exceptions.exception;

public class AccessDeniedException extends RuntimeException {
  public AccessDeniedException(String message) {
    super(message);
  }
}
