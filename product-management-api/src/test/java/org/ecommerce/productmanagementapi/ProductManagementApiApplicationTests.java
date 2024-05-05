package org.ecommerce.productmanagementapi;

import org.ecommerce.productmanagementapi.config.MockS3Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ProductManagementApiApplication.class)
@Import(MockS3Config.class)
@ActiveProfiles("test")
class ProductManagementApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
