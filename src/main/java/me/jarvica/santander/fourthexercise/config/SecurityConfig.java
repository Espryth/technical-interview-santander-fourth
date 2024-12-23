package me.jarvica.santander.fourthexercise.config;

import me.jarvica.santander.fourthexercise.auth.token.TokenFilter;
import me.jarvica.santander.fourthexercise.auth.service.OAuthSuccessHandler;
import me.jarvica.santander.fourthexercise.user.User;
import me.jarvica.santander.fourthexercise.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(
      final HttpSecurity http,
      final TokenFilter filter,
      final AuthenticationProvider authenticationProvider,
      final OAuthSuccessHandler successHandler
  ) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize ->
            authorize
                .requestMatchers("/", "/login", "/logout", "/login/oauth2/code/**", "/auth/oauth2/login/**", "/auth/**")
                .permitAll()
                .requestMatchers("/user")
                .hasAnyAuthority(User.Role.ADMIN.name(), User.Role.USER.name())
                .requestMatchers("/admin")
                .hasAuthority(User.Role.ADMIN.name())
                .anyRequest()
                .authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
        .oauth2Login(config -> config.successHandler(successHandler))
        .logout(Customizer.withDefaults())
        .build();
  }

  @Bean
  AuthenticationProvider authenticationProvider(final UserDetailsService service, final PasswordEncoder encoder) {
    final var provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(service);
    provider.setPasswordEncoder(encoder);
    return provider;
  }

  @Bean
  AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  UserDetailsService userDetailsService(final UserRepository userRepository) {
    return username -> userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
