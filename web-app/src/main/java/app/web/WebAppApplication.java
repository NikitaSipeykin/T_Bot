package app.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
    "app.web",
    "app.core",
    "app.text.node"
})
@EnableJpaRepositories(basePackages = "app.text.node.texts")
@EntityScan(basePackages = "app.text.node.texts")
public class WebAppApplication {
  public static void main(String[] args) {
    SpringApplication.run(WebAppApplication.class, args);
  }
}
