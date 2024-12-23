package me.jarvica.santander.fourthexercise.auth.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import me.jarvica.santander.fourthexercise.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class TokenServiceTest {

  @Mock
  private TokenRepository repository;

  @InjectMocks
  private TokenService service;

  private Token expiredToken;

  private Token validToken;

  @BeforeEach
  void setUp() {
    this.expiredToken = Token.builder()
        .id(1L)
        .token(
            Jwts.builder()
                .subject("test")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z6")))
                .compact()
        )
        .userId(UUID.randomUUID())
        .expired(false)
        .build();
    this.validToken = Token.builder()
        .id(1L)
        .token(
            Jwts.builder()
                .subject("test")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode("A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z6")))
                .compact()
        )
        .userId(UUID.randomUUID())
        .expired(false)
        .build();
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void generateTokenTest() {
    final var user = User.builder()
        .id(UUID.randomUUID())
        .username("test")
        .password("test")
        .build();
    final var token = service.createToken(user);
    when(repository.findByUserIdAndExpiredFalse(any())).thenReturn(List.of());
    when(repository.saveAll(any())).thenReturn(List.of());
    when(repository.save(any())).thenReturn(token);
    assertFalse(token.isExpired());
  }

  @Test
  void testGetUsername() {
    final var username = service.getUsername(validToken.getToken());
    assertEquals("test", username);
  }

  @Test
  void testGetValidToken() {
    when(repository.findByToken(anyString())).thenReturn(Optional.of(validToken));
    when(repository.save(any())).thenReturn(validToken);
    final var token = service.getToken(validToken.getToken());
    assertFalse(token.isExpired());
  }

  @Test
  void testGetExpiredToken() {
    when(repository.findByToken(anyString())).thenReturn(Optional.of(expiredToken));
    when(repository.save(any())).thenReturn(expiredToken);
    final var token = service.getToken(expiredToken.getToken());
    assertTrue(token.isExpired());
  }

}
