package org.ecommerce.productmanagementapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"org.ecommerce.product.*","org.ecommerce.productmanagementapi","org.ecommerce.common"})
@EntityScan("org.ecommerce.product.entity")
@EnableJpaRepositories("org.ecommerce.productmanagementapi.repository")
public class ProductManagementApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductManagementApiApplication.class, args);
  }

}
