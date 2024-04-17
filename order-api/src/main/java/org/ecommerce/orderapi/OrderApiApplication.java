package org.ecommerce.orderapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"org.ecommerce.common","org.ecommerce.orderapi"})
@EnableFeignClients
public class OrderApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrderApiApplication.class, args);
  }

}
