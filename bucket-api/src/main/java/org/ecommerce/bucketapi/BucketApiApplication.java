package org.ecommerce.bucketapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.ecommerce.common","org.ecommerce.bucketapi"})
public class BucketApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(BucketApiApplication.class, args);
  }

}
