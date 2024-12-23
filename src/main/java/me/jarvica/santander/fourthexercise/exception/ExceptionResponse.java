package me.jarvica.santander.fourthexercise.exception;

import java.util.List;

public record ExceptionResponse(
  String message,
  List<String> details
) {

  public static ExceptionResponse of(final String message, final String... details) {
    return new ExceptionResponse(message, List.of(details));
  }
}
