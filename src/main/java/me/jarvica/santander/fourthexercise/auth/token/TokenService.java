package me.jarvica.santander.fourthexercise.auth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import me.jarvica.santander.fourthexercise.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public final class TokenService {

  private final TokenRepository repository;

  @Value("${jwt.secret-key}")
  private String secretKey = "A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V2W3X4Y5Z6";

  @Value("${jwt.expiration}")
  private long expiration = 3600000;

  @Autowired
  TokenService(final TokenRepository repository) {
    this.repository = repository;
  }

  public Token generateToken(final User user) {
    final var tokens = this.repository.findByUserIdAndExpiredFalse(user.getId());
    tokens.forEach(token -> token.setExpired(true));
    this.repository.saveAll(tokens);
    return this.repository.save(createToken(user));
  }

  Token createToken(final User user) {
    return Token.builder()
        .userId(user.getId())
        .token(
            Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + this.expiration))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .compact()
        )
        .build();
  }

  public Token getToken(final String rawToken) {
    return this.repository.findByToken(rawToken)
        .map(token -> {
          if (this.isExpired(token.getToken())) {
            token.setExpired(true);
            return this.repository.save(token);
          }
          return token;
        })
        .orElse(null);
  }

  public String getUsername(final String token) {
    return this.getClaims(token).getSubject();
  }

  private boolean isExpired(final String token) {
    try {
      return this.getClaims(token)
          .getExpiration()
          .before(new Date());
    } catch (final ExpiredJwtException e) {
      return true;
    }
  }

  private Claims getClaims(final String token) {
    return Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

}
