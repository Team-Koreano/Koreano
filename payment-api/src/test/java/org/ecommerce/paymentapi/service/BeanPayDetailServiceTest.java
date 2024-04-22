
package org.ecommerce.paymentapi.service;

import static org.assertj.core.api.Assertions.*;
import static org.ecommerce.paymentapi.exception.BeanPayDetailErrorCode.*;
import static org.ecommerce.userapi.entity.type.Role.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.client.TossServiceClient;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.dto.BeanPayMapper;
import org.ecommerce.paymentapi.dto.TossDto;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.BeanPayDetail;
import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.ecommerce.paymentapi.entity.type.ProcessStatus;
import org.ecommerce.paymentapi.repository.BeanPayDetailRepository;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.ecommerce.paymentapi.utils.TossKey;
import org.ecommerce.userapi.entity.type.Role;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class BeanPayDetailServiceTest {

	@InjectMocks
	private BeanPayService beanPayService;

	@Mock
	private BeanPayRepository beanPayRepository;

	@Mock
	private BeanPayDetailRepository beanPayDetailRepository;

	@Mock
	private TossServiceClient tossServiceClient;

	@Mock
	private TossKey tossKey;

	@Test
	void 사전충전객체_생성() {

		//given
		final BeanPayDto.Request.PreCharge request = new BeanPayDto.Request.PreCharge(1, 10_000);
		final BeanPay beanPay = getBeanPay();
		final BeanPayDetail entity = BeanPayDetail.ofCreate(beanPay, 1, 10000);
		final BeanPayDto response = BeanPayMapper.INSTANCE.toDto(entity);

		given(beanPayRepository.findBeanPayByUserIdAndRole(any(), any(Role.class))).willReturn(Optional.of(beanPay));
		given(beanPayDetailRepository.save(any())).willReturn(entity);


		//when
		final BeanPayDto actual = beanPayService.preChargeBeanPay(request);

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

			final TossDto.Request.TossPayment request = new TossDto.Request.TossPayment(paymentType, paymentKey,
				orderId, amount);
			final BeanPayDetail entity = new BeanPayDetail(orderId, getBeanPay(), null, userId,
				amount, null, null, BeanPayStatus.DEPOSIT,
				ProcessStatus.PENDING, LocalDateTime.now(), null);
			final TossDto.Response.TossPayment response = new TossDto.Response.TossPayment(paymentType, orderName,
				method, amount, approveDateTime);
			final ResponseEntity<TossDto.Response.TossPayment> tossResponse = ResponseEntity.of(
				Optional.of(response));

			//when
			when(beanPayDetailRepository.findById(request.orderId())).thenReturn(Optional.of(entity));
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request)).thenReturn(tossResponse);

			//then
			assertDoesNotThrow(() -> beanPayService.validTossCharge(request));
		}

		@Test
		void 빈페이_존재안함_예외발생() {

			//given
			final UUID orderId = UUID.randomUUID();
			final String paymentKey = "paymentKey";
			final String paymentType = "카드";
			final Integer amount = 1000;

			final TossDto.Request.TossPayment request = new TossDto.Request.TossPayment(paymentType, paymentKey,
				orderId, amount);

			//when
			when(beanPayDetailRepository.findById(request.orderId())).thenThrow(
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

			final TossDto.Request.TossPayment request = new TossDto.Request.TossPayment(paymentType, paymentKey,
				orderId, amount);
			final BeanPayDetail entity = new BeanPayDetail(orderId, getBeanPay(), null, userId,
				difAmount, null, null, BeanPayStatus.DEPOSIT,
				ProcessStatus.PENDING, LocalDateTime.now(), null);

			//when
			when(beanPayDetailRepository.findById(request.orderId())).thenReturn(Optional.of(entity));
			Optional<BeanPayDetail> optionalBeanPay = beanPayDetailRepository.findById(request.orderId());

			//then
			assertDoesNotThrow(() -> beanPayService.validTossCharge(request));
			assertTrue(optionalBeanPay.isPresent());
			assertEquals(optionalBeanPay.get().getCancelOrFailReason(), VERIFICATION_FAIL.getMessage());
			assertEquals(ProcessStatus.CANCELLED, optionalBeanPay.get().getProcessStatus());
		}

		@Test
		void 토스검증승인_예외발생() {
			//given
			final UUID orderId = UUID.randomUUID();
			final Integer userId = 1;
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String paymentType = "카드";
			final String method = "카드";
			final Integer amount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";

			final TossDto.Request.TossPayment request = new TossDto.Request.TossPayment(paymentType, paymentKey,
				orderId, amount);
			final BeanPayDetail entity = new BeanPayDetail(orderId, getBeanPay(), null, userId,
				amount, null, null, BeanPayStatus.DEPOSIT,
				ProcessStatus.PENDING, LocalDateTime.now(), null);
			final TossDto.Response.TossPayment response = new TossDto.Response.TossPayment(paymentType, orderName,
				method, amount, approveDateTime);
			final ResponseEntity<TossDto.Response.TossPayment> tossFailResponse = ResponseEntity.status(400)
				.body(response);

			//when
			when(beanPayDetailRepository.findById(request.orderId())).thenReturn(Optional.of(entity));
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request)).thenReturn(tossFailResponse);
			Optional<BeanPayDetail> optionalBeanPay = beanPayDetailRepository.findById(request.orderId());

			//then
			assertDoesNotThrow(() -> beanPayService.validTossCharge(request));
			assertTrue(optionalBeanPay.isPresent());
			assertEquals(optionalBeanPay.get().getCancelOrFailReason(), TOSS_RESPONSE_FAIL.getMessage());
			assertEquals(ProcessStatus.CANCELLED, optionalBeanPay.get().getProcessStatus());
		}

	}

	@Nested
	class 토스사전결제_실패 {
		@Test
		void 성공() {
			//given
			final UUID orderId = UUID.randomUUID();
			final Integer userId = 1;
			final Integer amount = 1000;
			final String errorMessage = "사용자에 의해 결제가 취소되었습니다.";
			final String errorCode = "PAY_PROCESS_CANCELED";

			final BeanPayDto.Request.TossFail request = new BeanPayDto.Request.TossFail(orderId, errorCode, errorMessage);

			final BeanPayDetail entity = new BeanPayDetail(orderId, getBeanPay(), null, userId,
				amount, null, errorMessage,
				BeanPayStatus.DEPOSIT, ProcessStatus.CANCELLED, LocalDateTime.now(), null);

			//when
			when(beanPayDetailRepository.findById(request.orderId())).thenReturn(Optional.of(entity));
			Optional<BeanPayDetail> optionalBeanPay = beanPayDetailRepository.findById(request.orderId());

			//then
			assertDoesNotThrow(() -> beanPayService.failTossCharge(request));
			assertTrue(optionalBeanPay.isPresent());
			assertEquals(optionalBeanPay.get().getCancelOrFailReason(), errorMessage);
			assertEquals(ProcessStatus.CANCELLED, optionalBeanPay.get().getProcessStatus());
		}	
	}
	@Test
	void 빈페이_존재안함_예외발생() {
		//given
		final UUID orderId = UUID.randomUUID();
		final String errorMessage = "사용자에 의해 결제가 취소되었습니다.";
		final String errorCode = "PAY_PROCESS_CANCELED";

		final BeanPayDto.Request.TossFail request = new BeanPayDto.Request.TossFail(orderId, errorCode, errorMessage);

		//when
		when(beanPayDetailRepository.findById(request.orderId())).thenThrow(
			new CustomException(NOT_EXIST));

		//then
		CustomException returnException = assertThrows(CustomException.class, () -> {
			beanPayService.failTossCharge(request);
		});
		assertEquals(returnException.getErrorCode(), NOT_EXIST);
	}

	private BeanPay getBeanPay() {
		return new BeanPay(1, 1, USER, 0, LocalDateTime.now());
	}
}