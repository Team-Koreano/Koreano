package org.ecommerce.paymentapi.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BeanPayDtoTest {
	@Test
	void 사전충전DTO_생성() {
		//given
		Integer userId = 1;
		Integer amount = 10000;

		//when
		BeanPayDto.Request.PreCharge preCharge = new BeanPayDto.Request.PreCharge(userId, amount);

		//then
		assertEquals(preCharge.userId(), userId);
		assertEquals(preCharge.amount(), amount);

	}
}