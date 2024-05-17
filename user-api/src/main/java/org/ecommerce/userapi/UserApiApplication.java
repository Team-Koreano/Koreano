package org.ecommerce.userapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"org.ecommerce.common", "org.ecommerce.userapi"}
	, nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class UserApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApiApplication.class, args);
	}
}
