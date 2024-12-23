package me.jarvica.santander.fourthexercise.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(
    @NotBlank @NotNull String username,
    @NotBlank @NotNull String password
) {
}
