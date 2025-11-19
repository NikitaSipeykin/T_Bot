package app.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "app.web",
    "app.core"
})
public class WebAppApplication {
  public static void main(String[] args) {
    SpringApplication.run(WebAppApplication.class, args);
  }
}
