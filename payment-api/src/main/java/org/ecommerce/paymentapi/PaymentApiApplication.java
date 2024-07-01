package org.ecommerce.paymentapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@EnableFeignClients
@SpringBootApplication(scanBasePackages = {
	"org.ecommerce.common",
	"org.ecommerce.paymentapi",
	"org.ecommerce.redis"
}, nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class PaymentApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentApiApplication.class, args);

	}

}
