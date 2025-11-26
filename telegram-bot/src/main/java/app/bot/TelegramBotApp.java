package app.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
    "app.bot",
    "app.core",
    "app.text.node",
    "app.video.node"
})
@EnableJpaRepositories({"app.text.node.texts", "app.video.node.web"})
@EntityScan(basePackages = {"app.text.node.texts", "app.video.node.web"})
public class TelegramBotApp {
  public static void main(String[] args) {
    SpringApplication.run(TelegramBotApp.class, args);
  }
}

