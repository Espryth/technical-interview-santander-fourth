package me.jarvica.santander.fourthexercise.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.jarvica.santander.fourthexercise.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class OAuthSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final Authentication authentication
  ) {
    final var oauthAuthentication = (OAuth2AuthenticationToken) authentication;
    SecurityContextHolder.getContext().setAuthentication(
        new OAuth2AuthenticationToken(
            oauthAuthentication.getPrincipal(),
            List.of(User.Role.USER),
            oauthAuthentication.getAuthorizedClientRegistrationId()
        )
    );
  }
}
