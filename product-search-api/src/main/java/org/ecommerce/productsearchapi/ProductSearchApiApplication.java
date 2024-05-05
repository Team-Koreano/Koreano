package org.ecommerce.productsearchapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(scanBasePackages = {"org.ecommerce.product","org.ecommerce.productsearchapi", "org.ecommerce.common"}, nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class ProductSearchApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductSearchApiApplication.class, args);
  }

}
