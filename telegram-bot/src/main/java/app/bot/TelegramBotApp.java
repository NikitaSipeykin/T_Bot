package app.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "app")
@EntityScan(basePackages = "app")
@ComponentScan(basePackages = "app")
public class TelegramBotApp {
  public static void main(String[] args) {
    SpringApplication.run(TelegramBotApp.class, args);
  }
}

