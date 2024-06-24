package org.ecommerce.productapi;

import org.ecommerce.productapi.config.MockS3Config;
import org.ecommerce.productapi.repository.ProductElasticsearchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ProductApiApplication.class)
@Import(MockS3Config.class)
@ActiveProfiles("test")
class ProductApiApplicationTests {
	
	@MockBean
	private ProductElasticsearchRepository productElasticsearchRepository;

	@Test
	void contextLoads() {
	}

}
