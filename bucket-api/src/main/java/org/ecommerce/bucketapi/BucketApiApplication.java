package org.ecommerce.bucketapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(scanBasePackages =
        {"org.ecommerce.common","org.ecommerce.bucketapi"},
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class BucketApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(BucketApiApplication.class, args);
  }

}
