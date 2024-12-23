package me.jarvica.santander.fourthexercise.auth.token;

import me.jarvica.santander.fourthexercise.DatabaseBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TokenRepositoryTest extends DatabaseBaseTest {

  @Autowired
  private TokenRepository repository;

  @Test
  void testFindByToken() {
    final var token = "test";
    this.repository.save(Token.builder().token(token).build());
    assertTrue(this.repository.findByToken(token).isPresent());
  }

  @Test
  void testFindByUserIdAndExpiredFalse() {
    final var id = UUID.randomUUID();
    final var tokens = List.of(
        Token.builder().userId(id).expired(false).build(),
        Token.builder().userId(id).expired(true).build(),
        Token.builder().userId(id).expired(false).build()
    );
    this.repository.saveAll(tokens);
    assertEquals(2, this.repository.findByUserIdAndExpiredFalse(id).size());
  }

}
