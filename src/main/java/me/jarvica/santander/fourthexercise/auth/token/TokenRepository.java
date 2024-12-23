package me.jarvica.santander.fourthexercise.auth.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, Long> {

  Optional<Token> findByToken(final String token);

  List<Token> findByUserIdAndExpiredFalse(final UUID userId);

}
