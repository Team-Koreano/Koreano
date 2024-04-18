package org.ecommerce.paymentapi.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class BeanPayDtoTest {

	private static ValidatorFactory factory;
	private static Validator validator;

	@BeforeAll
	public static void init() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

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

	@Nested
	class 토스결제승인_검증_DTO {
		@Test
		void 성공() {
			//given
			final UUID orderId = UUID.randomUUID();
			final String paymentKey = "paymentKey";
			final String paymentType = "카드";
			final Integer amount = 1000;

			//when
			final BeanPayDto.Request.TossPayment request =
				new BeanPayDto.Request.TossPayment(paymentType, paymentKey, orderId, amount);

			//then
			assertEquals(orderId, request.orderId());
			assertEquals(paymentKey, request.paymentKey());
			assertEquals(amount, request.amount());
			assertEquals(paymentType, request.paymentType());
		}
		@Test
		void 실패() {
			//given
			final UUID orderId = null;
			final String paymentKey = "";
			final String paymentType = "";
			final Integer amount = -1;

			//when
			final BeanPayDto.Request.TossPayment request =
				new BeanPayDto.Request.TossPayment(paymentType, paymentKey, orderId, amount);

			//then
			Set<ConstraintViolation<BeanPayDto.Request.TossPayment>> violations =
				validator.validate(request);

			assertEquals(5, violations.size());
		}
	}

	@Nested
	class 토스사전결제_실패_DTO {
		@Test
		void 성공() {
			//given
			final UUID orderId = UUID.randomUUID();
			final String errorMessage = "사용자에 의해 결제가 취소되었습니다.";
			final String errorCode = "PAY_PROCESS_CANCELED";

			//when
			final BeanPayDto.Request.TossFail request =
				new BeanPayDto.Request.TossFail(orderId, errorCode, errorMessage);

			//then
			assertEquals(orderId, request.orderId());
			assertEquals(errorMessage, request.errorMessage());
			assertEquals(errorCode, request.errorCode());
		}
		@Test
		void 실패() {
			//given
			final UUID orderId = null;
			final String errorMessage = "";
			final String errorCode = "";

			//when
			final BeanPayDto.Request.TossFail request =
				new BeanPayDto.Request.TossFail(orderId, errorMessage, errorCode);

			//then
			Set<ConstraintViolation<BeanPayDto.Request.TossFail>> violations =
				validator.validate(request);

			assertEquals(3, violations.size());
		}
	}

}