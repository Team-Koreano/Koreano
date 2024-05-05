package org.ecommerce.paymentapi.entity;

import static org.ecommerce.paymentapi.entity.enumerate.Role.*;
import static org.ecommerce.paymentapi.exception.BeanPayDetailErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.ecommerce.paymentapi.dto.TossDto;
import org.ecommerce.paymentapi.entity.enumerate.BeanPayStatus;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BeanPayDetailTest {

	@Test
	void 충전엔티티_생성() {
		//given
		Integer userId = 1;
		Integer amount = 10000;

		//when
		BeanPayDetail actual = BeanPayDetail.ofCreate(getBeanPay(), userId, amount);

		//then
		assertEquals(actual.getAmount(), amount);
		assertEquals(actual.getUserId(), userId);
	}

	@Nested
	class 충전로직 {
		@Test
		void 충전로직_검증() {
			//given
			final Integer userId = 1;
			final Integer amount = 1000;
			final UUID orderId = UUID.randomUUID();
			final String paymentKey = "paymentKey";
			final String paymentType = "카드";
			final BeanPayDetail actual = getBeanPayDetail(orderId, paymentKey, userId, amount, paymentType);

			final TossDto.Request.TossPayment request =
				new TossDto.Request.TossPayment(paymentType, paymentKey, orderId, amount);

			//when
			boolean flag = actual.validBeanPay(request.orderId(), request.amount());

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
			final BeanPayDetail actual = getBeanPayDetail(orderId, paymentKey, userId, amount, paymentType);

			final TossDto.Request.TossPayment request =
				new TossDto.Request.TossPayment(paymentType, paymentKey, orderId, amount);
			final TossDto.Response.TossPayment response =
				new TossDto.Response.TossPayment(paymentType, orderName, method, amount, approveDateTime);
			//when
			actual.chargeComplete(response);

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
			final BeanPayDetail actual = getBeanPayDetail(orderId, paymentKey, userId, amount, paymentType);

			//when
			actual.chargeFail(TOSS_RESPONSE_FAIL.getMessage());

			//then
			assertEquals(actual.getProcessStatus(), ProcessStatus.FAILED);
			assertEquals(actual.getFailReason(), TOSS_RESPONSE_FAIL.getMessage());
		}
	}



	private BeanPayDetail getBeanPayDetail(
		UUID orderId,
		String paymentKey,
		Integer userId,
		Integer amount,
		String payType)
	{
		return new BeanPayDetail(
			orderId,
			getBeanPay(),
			paymentKey,
			userId,
			amount,
			payType,
			null,
			null,
			BeanPayStatus.DEPOSIT,
			ProcessStatus.PENDING,
			LocalDateTime.now(),
			null
		);
	}

	private BeanPay getBeanPay() {
		return new BeanPay(1, 1, USER, 0, LocalDateTime.now());
	}

}