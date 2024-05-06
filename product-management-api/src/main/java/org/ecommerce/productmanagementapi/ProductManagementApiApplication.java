package org.ecommerce.productmanagementapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(scanBasePackages = {"org.ecommerce.product.*", "org.ecommerce.productmanagementapi",
	"org.ecommerce.common"}, nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class ProductManagementApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductManagementApiApplication.class, args);
	}

}
