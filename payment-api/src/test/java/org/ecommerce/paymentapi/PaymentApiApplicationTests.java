package org.ecommerce.paymentapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("local")
@SpringBootTest(classes = PaymentApiApplication.class)
class PaymentApiApplicationTests {

  @Test
  void contextLoads() {
  }

}
