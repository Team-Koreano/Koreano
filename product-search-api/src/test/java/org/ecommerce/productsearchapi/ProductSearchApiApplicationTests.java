package org.ecommerce.productsearchapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ProductSearchApiApplication.class)
@ActiveProfiles("test")
class ProductSearchApiApplicationTests {

  @Test
  void contextLoads() {
  }

}
