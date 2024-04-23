package org.ecommerce.productmanagementapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ProductManagementApiApplication.class)
@ActiveProfiles("test")
class ProductManagementApiApplicationTests {

  @Test
  void contextLoads() {
  }

}
