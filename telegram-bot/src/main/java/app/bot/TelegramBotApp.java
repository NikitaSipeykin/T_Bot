package app.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "app.bot",
    "app.core"
})
public class TelegramBotApp {
  public static void main(String[] args) {
    SpringApplication.run(TelegramBotApp.class, args);
  }
}

