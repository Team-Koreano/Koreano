package org.ecommerce.paymentapi.service;

import static org.assertj.core.api.Assertions.*;
import static org.ecommerce.paymentapi.exception.BeanPayErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.client.TossServiceClient;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.ecommerce.paymentapi.entity.type.ProcessStatus;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.ecommerce.paymentapi.utils.TossKey;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class BeanPayServiceTest {

	@InjectMocks
	private BeanPayService beanPayService;

	@Mock
	private BeanPayRepository beanPayRepository;

	@Mock
	private TossServiceClient tossServiceClient;

	@Mock
	private TossKey tossKey;

	@Test
	void 사전충전객체_생성() {

		//given
		final BeanPayDto.Request.PreCharge request = new BeanPayDto.Request.PreCharge(1, 10_000);
		final BeanPay entity = BeanPay.ofCreate(1, 10000);
		final BeanPayDto.Response response = BeanPayDto.Response.ofCreate(entity);

		given(beanPayRepository.save(any())).willReturn(entity);

		//when
		final BeanPayDto.Response actual = beanPayService.preChargeBeanPay(request);

		//then
		assertThat(actual).usingRecursiveComparison().isEqualTo(response);
	}

	@Nested
	class 토스결제_검증 {
		@Test
		void 성공() {
			//given
			final UUID orderId = UUID.randomUUID();
			final Integer userId = 1;
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String paymentType = "카드";
			final String method = "카드";
			final Integer amount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";

			final BeanPayDto.Request.TossPayment request = new BeanPayDto.Request.TossPayment(paymentType, paymentKey,
				orderId, amount);
			final BeanPay entity = new BeanPay(orderId, null, userId, amount, null, null, BeanPayStatus.DEPOSIT,
				ProcessStatus.PENDING, LocalDateTime.now(), null);
			final BeanPayDto.Response.TossPayment response = new BeanPayDto.Response.TossPayment(paymentType, orderName,
				method, amount, approveDateTime);
			final ResponseEntity<BeanPayDto.Response.TossPayment> tossResponse = ResponseEntity.of(
				Optional.of(response));

			//when
			when(beanPayRepository.findById(request.orderId())).thenReturn(Optional.of(entity));
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request)).thenReturn(tossResponse);

			//then
			assertDoesNotThrow(() -> beanPayService.validTossCharge(request));
		}

		@Test
		void 빈페이_존재_안함_예외발생() {

			//given
			final UUID orderId = UUID.randomUUID();
			final String paymentKey = "paymentKey";
			final String paymentType = "카드";
			final Integer amount = 1000;

			final BeanPayDto.Request.TossPayment request = new BeanPayDto.Request.TossPayment(paymentType, paymentKey,
				orderId, amount);

			//when
			when(beanPayRepository.findById(request.orderId())).thenThrow(
				new CustomException(NOT_EXIST));

			//then
			CustomException returnException = assertThrows(CustomException.class, () -> {
				beanPayService.validTossCharge(request);
			});
			assertEquals(returnException.getErrorCode(), NOT_EXIST);
		}

		@Test
		void 빈페이_검증값_불일치_예외발생() {
			//given
			final UUID orderId = UUID.randomUUID();
			final Integer userId = 1;
			final String paymentKey = "paymentKey";
			final String paymentType = "카드";
			final Integer amount = 1000;
			final Integer difAmount = 10000;

			final BeanPayDto.Request.TossPayment request = new BeanPayDto.Request.TossPayment(paymentType, paymentKey,
				orderId, amount);
			final BeanPay entity = new BeanPay(orderId, null, userId, difAmount, null, null, BeanPayStatus.DEPOSIT,
				ProcessStatus.PENDING, LocalDateTime.now(), null);

			//when
			when(beanPayRepository.findById(request.orderId())).thenReturn(Optional.of(entity));

			//then
			CustomException returnException = assertThrows(CustomException.class, () -> {
				beanPayService.validTossCharge(request);
			});
			assertEquals(returnException.getErrorCode(), VERIFICATION_FAIL);
		}

		@Test
		void 토스_검증_승인_예외발생() {
			//given
			final UUID orderId = UUID.randomUUID();
			final Integer userId = 1;
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String paymentType = "카드";
			final String method = "카드";
			final Integer amount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";

			final BeanPayDto.Request.TossPayment request = new BeanPayDto.Request.TossPayment(paymentType, paymentKey,
				orderId, amount);
			final BeanPay entity = new BeanPay(orderId, null, userId, amount, null, null, BeanPayStatus.DEPOSIT,
				ProcessStatus.PENDING, LocalDateTime.now(), null);
			final BeanPayDto.Response.TossPayment response = new BeanPayDto.Response.TossPayment(paymentType, orderName,
				method, amount, approveDateTime);
			final ResponseEntity<BeanPayDto.Response.TossPayment> tossFailResponse = ResponseEntity.status(400)
				.body(response);

			//when
			when(beanPayRepository.findById(request.orderId())).thenReturn(Optional.of(entity));
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request)).thenReturn(tossFailResponse);

			//then
			CustomException returnException = assertThrows(CustomException.class, () -> {
				beanPayService.validTossCharge(request);
			});
			assertEquals(returnException.getErrorCode(), TOSS_RESPONSE_FAIL);
		}

	}

}