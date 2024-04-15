package org.ecommerce.paymentapi.entity;

import static org.ecommerce.paymentapi.exception.BeanPayErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.ecommerce.paymentapi.entity.type.ProcessStatus;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.junit.jupiter.api.Nested;
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

	@Nested
	class 충전로직 {
		@Test
		void 충전로직_진행중() {
			//given
			final Integer userId = 1;
			final Integer amount = 10000;
			final BeanPay actual = BeanPay.ofCreate(userId, amount);

			//when
			actual.inProgress();

			//then
			assertEquals(actual.getProcessStatus(), ProcessStatus.IN_PROGRESS);
		}

		@Test
		void 충전로직_검증() {
			//given
			final Integer userId = 1;
			final Integer amount = 1000;
			final UUID orderId = UUID.randomUUID();
			final String paymentKey = "paymentKey";
			final String paymentType = "카드";
			final BeanPay actual = getBeanPay(orderId, paymentKey, userId, amount, paymentType);

			final BeanPayDto.Request.TossPayment request =
				new BeanPayDto.Request.TossPayment(paymentType, paymentKey, orderId, amount);

			//when
			boolean flag = actual.validBeanPay(request);

			//then
			assertTrue(flag);
		}

		@Test
		void 충전로직_완료() {
			//given

			final UUID orderId = UUID.randomUUID();
			final Integer userId = 1;
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String paymentType = "카드";
			final String method = "카드";
			final Integer amount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
			LocalDateTime formatApproveTime = LocalDateTime.parse(approveDateTime, formatter);
			final BeanPay actual = getBeanPay(orderId, paymentKey, userId, amount, paymentType);

			final BeanPayDto.Request.TossPayment request =
				new BeanPayDto.Request.TossPayment(paymentType, paymentKey, orderId, amount);
			final BeanPayDto.Response.TossPayment response =
				new BeanPayDto.Response.TossPayment(paymentType, orderName, method, amount, approveDateTime);
			//when
			actual.complete(response);

			//then
			assertEquals(actual.getProcessStatus(), ProcessStatus.COMPLETED);
			assertEquals(actual.getUserId(), userId);
			assertEquals(actual.getAmount(), amount);
			assertEquals(actual.getProcessStatus(), ProcessStatus.COMPLETED);
			assertEquals(actual.getApproveDateTime(), formatApproveTime);
		}

		@Test
		void 충전로직_실패() {
			//given

			final UUID orderId = UUID.randomUUID();
			final Integer userId = 1;
			final String paymentKey = "paymentKey";
			final String paymentType = "카드";
			final Integer amount = 1000;
			final BeanPay actual = getBeanPay(orderId, paymentKey, userId, amount, paymentType);

			//when
			actual.fail(TOSS_RESPONSE_FAIL);

			//then
			assertEquals(actual.getProcessStatus(), ProcessStatus.CANCELLED);
			assertEquals(actual.getCancelOrFailReason(), TOSS_RESPONSE_FAIL.getMessage());
		}
	}



	BeanPay getBeanPay(UUID orderId, String paymentKey, Integer userId, Integer amount, String payType) {
		return new BeanPay(orderId, paymentKey, userId, amount, payType, null, BeanPayStatus.DEPOSIT,
			ProcessStatus.PENDING, LocalDateTime.now(), null);
	}

}