package org.ecommerce.productapi;

import org.ecommerce.productapi.config.MockS3Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ProductApiApplication.class)
@Import(MockS3Config.class)
@ActiveProfiles("test")
class ProductApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
