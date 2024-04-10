package org.ecommerce.productmanagementapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//TODO 데이터베이스 연동 시 제거
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ProductManagementApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductManagementApiApplication.class, args);
  }

}
