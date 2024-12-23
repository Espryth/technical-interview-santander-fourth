package me.jarvica.santander.fourthexercise.auth.service;

import me.jarvica.santander.fourthexercise.auth.request.AuthRequest;
import me.jarvica.santander.fourthexercise.auth.response.AuthFailedResponse;
import me.jarvica.santander.fourthexercise.auth.response.AuthSuccessResponse;
import me.jarvica.santander.fourthexercise.auth.token.TokenService;
import me.jarvica.santander.fourthexercise.user.User;
import me.jarvica.santander.fourthexercise.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public final class AuthService {

  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final TokenService tokenService;

  @Autowired
  AuthService(
      final AuthenticationManager authenticationManager,
      final PasswordEncoder passwordEncoder,
      final UserRepository userRepository,
      final TokenService tokenService
  ) {
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.tokenService = tokenService;
  }

  public ResponseEntity<Object> register(final AuthRequest request) {
    return this.userRepository.findByUsername(request.username())
        .<ResponseEntity<Object>> map(user ->
            ResponseEntity.status(HttpStatus.CONFLICT)
                .body(AuthFailedResponse.of("User already exists"))
        )
        .orElseGet(() -> {
          final var user = this.userRepository.save(
              User.builder()
                  .username(request.username())
                  .password(passwordEncoder.encode(request.password()))
                  .role(User.Role.USER)
                  .build()
          );
          final var token = this.tokenService.generateToken(user);
          return ResponseEntity.ok(AuthSuccessResponse.of(token.getToken()));
        });
  }

  public ResponseEntity<Object> login(final AuthRequest request) {
    this.authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password())
    );
    final var user = this.userRepository.findByUsername(request.username())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    final var token = this.tokenService.generateToken(user);
    return ResponseEntity.ok(AuthSuccessResponse.of(token.getToken()));
  }

}
