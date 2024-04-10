package org.ecommerce.paymentapi.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.ecommerce.paymentapi.entity.type.ProcessStatus;
import org.ecommerce.paymentapi.service.BeanPayService;
import org.junit.jupiter.api.Test;

class BeanPayTest {

	@Test
	void 충전엔티티_생성() {
		//given
		Integer userId = 1;
		Integer amount = 10000;

		//when
		BeanPay actual = BeanPay.ofCreate(userId, amount);

		//then
		assertEquals(actual.getAmount(), amount);
		assertEquals(actual.getUserId(), userId);
	}
}