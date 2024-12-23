package me.jarvica.santander.fourthexercise.auth.response;

public record AuthFailedResponse(
    String message
) {

  public static AuthFailedResponse of(final String message) {
    return new AuthFailedResponse(message);
  }

}
