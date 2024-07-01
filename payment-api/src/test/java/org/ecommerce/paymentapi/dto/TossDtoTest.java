package org.ecommerce.paymentapi.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.UUID;

import org.ecommerce.paymentapi.dto.request.TossPaymentRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class TossDtoTest {
	private static ValidatorFactory factory;
	private static Validator validator;

	@BeforeAll
	public static void init() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
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
			final Integer userId = 1;

			//when
			final TossPaymentRequest request =
				new TossPaymentRequest(paymentType, paymentKey, orderId, amount, userId);

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
			final Integer userId = 1;

			//when
			final TossPaymentRequest request =
				new TossPaymentRequest(paymentType, paymentKey, orderId, amount, userId);

			//then
			Set<ConstraintViolation<TossPaymentRequest>> violations =
				validator.validate(request);

			assertEquals(5, violations.size());
		}
	}
}
