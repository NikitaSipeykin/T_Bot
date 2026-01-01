package app.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaRepositories(basePackages = "app")
@EntityScan(basePackages = "app")
@ComponentScan(basePackages = "app")
public class WebAppApplication {
  public static void main(String[] args) {
    SpringApplication.run(WebAppApplication.class, args);
  }
}
