package org.example.shelf_market.exceptions;

public class ValidationException extends BaseException {
  public ValidationException(String message) {
    super(message, "VALIDATION_ERROR");
  }
}