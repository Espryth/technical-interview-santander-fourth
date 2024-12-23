package me.jarvica.santander.fourthexercise.auth.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.jarvica.santander.fourthexercise.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public final class TokenFilter extends OncePerRequestFilter {

  private final TokenService tokenService;
  private final UserDetailsService userDetailsService;

  @Autowired
  TokenFilter(final TokenService tokenService, final UserDetailsService userDetailsService) {
    this.tokenService = tokenService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain
  ) throws ServletException, IOException {
   if (request.getServletPath().startsWith("/auth")) {
     filterChain.doFilter(request, response);
     return;
   }
    final var bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    final var rawToken = bearerToken.substring(7);
    final var token = this.tokenService.getToken(rawToken);
    if (token == null || token.isExpired()) {
      filterChain.doFilter(request, response);
      return;
    }
    final var username = this.tokenService.getUsername(rawToken);
    if (username == null) {
      filterChain.doFilter(request, response);
      return;
    }
    try {
      final var user = (User) this.userDetailsService.loadUserByUsername(username);
      if (!user.getId().equals(token.getUserId())) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
      final var authenticationToken = new UsernamePasswordAuthenticationToken(
          user.getUsername(), user.getPassword(), user.getAuthorities()
      );
      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      filterChain.doFilter(request, response);
    } catch (final UsernameNotFoundException e) {
      filterChain.doFilter(request, response);
    }
  }
}
