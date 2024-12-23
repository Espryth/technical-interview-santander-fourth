package me.jarvica.santander.fourthexercise;

import org.junit.jupiter.api.TestInstance;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class DatabaseBaseTest {

  @Container
  protected static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>("postgres:latest")
      .withDatabaseName("test")
      .withUsername("test")
      .withPassword("test");

  static {
    CONTAINER.start();
  }

  @DynamicPropertySource
  static void setProperties(final DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", CONTAINER::getUsername);
    registry.add("spring.datasource.password", CONTAINER::getPassword);
  }
}