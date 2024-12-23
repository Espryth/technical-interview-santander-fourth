package me.jarvica.santander.fourthexercise.user;

import me.jarvica.santander.fourthexercise.DatabaseBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserRepositoryTest extends DatabaseBaseTest {

  @Autowired
  private UserRepository repository;

  @Test
  void testFindByUsername() {
    final var username = "test";
    this.repository.save(User.builder().username(username).build());
    assertTrue(this.repository.findByUsername(username).isPresent());
  }
}
