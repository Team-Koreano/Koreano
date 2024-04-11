package org.ecommerce.userapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"org.ecommerce.common", "org.ecommerce.userapi"}, exclude = SecurityAutoConfiguration.class)
public class UserApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(UserApiApplication.class, args);
  }
}
