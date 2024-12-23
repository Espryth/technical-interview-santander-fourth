package me.jarvica.santander.fourthexercise.auth.controller;

import jakarta.validation.Valid;
import me.jarvica.santander.fourthexercise.auth.request.AuthRequest;
import me.jarvica.santander.fourthexercise.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  @Autowired
  AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  ResponseEntity<Object> login(final @Valid @RequestBody AuthRequest request) {
    return this.authService.login(request);
  }

  @PostMapping("/register")
  ResponseEntity<Object> register(final @Valid @RequestBody AuthRequest request) {
    return this.authService.register(request);
  }
}