package org.ecommerce.productapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(scanBasePackages = {"org.ecommerce.common", "org.ecommerce.productapi"},
	nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class ProductApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductApiApplication.class, args);
	}
}
