package me.jarvica.santander.fourthexercise.auth.response;

public record AuthSuccessResponse(
    String token
) {

  public static AuthSuccessResponse of(final String token) {
    return new AuthSuccessResponse(token);
  }
}