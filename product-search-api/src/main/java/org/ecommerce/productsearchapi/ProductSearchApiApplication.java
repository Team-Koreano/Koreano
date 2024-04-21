package org.ecommerce.productsearchapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"org.ecommerce.product","org.ecommerce.productsearchapi"})
@EntityScan(basePackages = {"org.ecommerce.product"})
@EnableJpaRepositories(basePackages = {"org.ecommerce.productsearchapi"})
public class ProductSearchApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductSearchApiApplication.class, args);
  }

}
