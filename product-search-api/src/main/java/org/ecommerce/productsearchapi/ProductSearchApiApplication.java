package org.ecommerce.productsearchapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.ecommerce.product","org.ecommerce.productsearchapi"})
public class ProductSearchApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductSearchApiApplication.class, args);
  }

}
