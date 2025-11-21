package app.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Value("${web.login}")
  private String login;

  @Value("${web.password}")
  private String password;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // отключаем CSRF (не форма логина, всё ок)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/css/**", "/js/**").permitAll() // разрешаем статику
            .anyRequest().authenticated() // всё остальное — под логином
        )
        .httpBasic(Customizer.withDefaults()) // простая basic-auth
        .formLogin(Customizer.withDefaults()); // или форма логина

    return http.build();
  }

  @Bean
  public UserDetailsService users() {
    return new InMemoryUserDetailsManager(
        User.builder()
            .username(login)
            .password(passwordEncoder().encode(password))
            .roles("ADMIN")
            .build()
    );
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
