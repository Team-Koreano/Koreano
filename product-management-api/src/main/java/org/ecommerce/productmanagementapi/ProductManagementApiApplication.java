package org.ecommerce.productmanagementapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.ecommerce.product.*","org.ecommerce.productmanagementapi","org.ecommerce.common"})
public class ProductManagementApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductManagementApiApplication.class, args);
  }

}
