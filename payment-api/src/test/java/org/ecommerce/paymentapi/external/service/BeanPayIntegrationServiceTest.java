package org.ecommerce.paymentapi.external.service;

import static org.ecommerce.paymentapi.entity.enumerate.PaymentStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;
import static org.ecommerce.paymentapi.exception.PaymentDetailErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.client.TossServiceClient;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.request.PreChargeRequest;
import org.ecommerce.paymentapi.dto.request.TossFailRequest;
import org.ecommerce.paymentapi.dto.request.TossPaymentRequest;
import org.ecommerce.paymentapi.dto.response.TossPaymentResponse;
import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.ecommerce.paymentapi.internal.service.BeanPayService;
import org.ecommerce.paymentapi.repository.ChargeInfoRepository;
import org.ecommerce.paymentapi.repository.PaymentDetailRepository;
import org.ecommerce.paymentapi.repository.UserBeanPayRepository;
import org.ecommerce.paymentapi.utils.TossKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Execution(ExecutionMode.SAME_THREAD)
@ActiveProfiles("test")
@SpringBootTest
public class BeanPayIntegrationServiceTest {

	@Autowired
	private BeanPayService internalBeanPayService;

	@Autowired
	private org.ecommerce.paymentapi.external.service.BeanPayService externalBeanPayService;

	@MockBean
	private TossServiceClient tossServiceClient;

	@Autowired
	private TossKey tossKey;
	@Autowired
	private UserBeanPayRepository beanPayRepository;
	@Autowired
	private PaymentDetailRepository paymentDetailRepository;

	@Autowired
	private ChargeInfoRepository chargeInfoRepository;

	private UserBeanPay userBeanPay;

	@Transactional
	@BeforeEach
	public void 사전() {
		this.userBeanPay = UserBeanPay.ofCreate(1);
		beanPayRepository.saveAndFlush(this.userBeanPay);
	}

	@AfterEach
	public void 사후() {
		paymentDetailRepository.deleteAllInBatch();
		chargeInfoRepository.deleteAllInBatch();
		beanPayRepository.deleteAllInBatch();
	}

	@Test
	void 사전객체생성() {
		//given
		final Integer amount = 10_000;
		final Integer userId = 1;
		final PreChargeRequest request = new PreChargeRequest(
			amount
		);

		//when
		PaymentDetailDto actual =
			externalBeanPayService.beforeCharge(userId, request);

		//then
		assertEquals(actual.paymentAmount(), amount);
		assertEquals(actual.userId(), userId);
	}

	@Nested
	class 토스검증 {

		@Test
		void 성공() {
			//사전 결제
			final Integer amount = 10_000;
			final Integer userId = 1;
			final PreChargeRequest preCharge = new PreChargeRequest(
				amount
			);
			PaymentDetailDto dto =
				externalBeanPayService.beforeCharge(userId, preCharge);

			//given
			final String paymentType = "paymentType";
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String approveDateTime = "2024-04-14T17:41:52+09:00";
			final UUID orderId = dto.id();
			final TossPaymentRequest request = new TossPaymentRequest(paymentType, paymentKey,
				orderId, amount, userId);
			final TossPaymentResponse response = new TossPaymentResponse(paymentKey, orderName,
				paymentType, amount, approveDateTime);
			final ResponseEntity<TossPaymentResponse> tossResponse =
				new ResponseEntity<>(response, HttpStatus.OK);

			//when
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request))
				.thenReturn(tossResponse);
			PaymentDetailDto actual = externalBeanPayService.validTossCharge(request);

			//then
			assertNotNull(actual);
			assertEquals(actual.id(), orderId);
			assertEquals(actual.userId(), dto.userId());
			assertNull(actual.sellerId());
			assertEquals(actual.paymentAmount(), amount);
			assertNull(actual.cancelReason());
			assertNull(actual.failReason());
			assertEquals(actual.paymentStatus(), DEPOSIT);
			assertEquals(actual.processStatus(), COMPLETED);
			assertNotNull(actual.approveDateTime());
		}

		@Test
		void 중복API_호출() {
			//사전 결제
			final Integer amount = 10_000;
			final Integer userId = 1;
			final PreChargeRequest preCharge = new PreChargeRequest(
				amount
			);
			PaymentDetailDto dto =
				externalBeanPayService.beforeCharge(userId, preCharge);

			//given
			final String paymentType = "paymentType";
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String approveDateTime = "2024-04-14T17:41:52+09:00";
			final UUID orderId = dto.id();
			final TossPaymentRequest request = new TossPaymentRequest(paymentType, paymentKey,
				orderId, amount, userId);
			final TossPaymentResponse response = new TossPaymentResponse(paymentKey, orderName,
				paymentType, amount, approveDateTime);
			final ResponseEntity<TossPaymentResponse> tossResponse =
				new ResponseEntity<>(response, HttpStatus.OK);

			//when
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request))
				.thenReturn(tossResponse);
			externalBeanPayService.validTossCharge(request);
			PaymentDetailDto actual = externalBeanPayService.validTossCharge(request);

			// then
			assertNotNull(actual);
			assertEquals(actual.id(), orderId);
			assertEquals(actual.userId(), dto.userId());
			assertNull(actual.sellerId());
			assertEquals(actual.paymentAmount(), amount);
			assertNull(actual.cancelReason());
			assertEquals(actual.failReason(), DUPLICATE_API_CALL.getMessage());
			assertEquals(actual.paymentStatus(), DEPOSIT);
			assertEquals(actual.processStatus(), FAILED);
			assertNotNull(actual.approveDateTime());
		}
	}

	@Nested
	class 토스실패 {
		@Test
		void 성공() {

			//given
			final Integer userId = 1;
			final Integer amount = 1000;
			PaymentDetailDto dto = 사전결제(userId, amount);
			final UUID orderId = dto.id();
			final String errorMessage = "사용자에 의해 결제가 취소되었습니다.";
			final String errorCode = "PAY_PROCESS_CANCELED";

			final TossFailRequest request = new TossFailRequest(orderId, errorCode, errorMessage);

			//when
			PaymentDetailDto result = externalBeanPayService.failTossCharge(request);

			//then
			assertEquals(result.failReason(), errorMessage);
			assertEquals(result.processStatus(), ProcessStatus.FAILED);
			assertEquals(result.paymentStatus(), DEPOSIT);
		}
		@Test
		void 결제상세_존재안함_예외발생() {
			//given
			final Integer userId = 1;
			final Integer amount = 1000;
			사전결제(userId, amount);
			final UUID notExistId = UUID.randomUUID();
			final String errorMessage = "사용자에 의해 결제가 취소되었습니다.";
			final String errorCode = "PAY_PROCESS_CANCELED";

			final TossFailRequest request = new TossFailRequest(notExistId, errorCode, errorMessage);

			//when
			CustomException actual = assertThrows(CustomException.class, () -> {
				externalBeanPayService.failTossCharge(request);
			});
			//then
			assertEquals(actual.getErrorCode(), NOT_FOUND_ID);
		}

		private PaymentDetailDto 사전결제(Integer userId, Integer amount) {
			final PreChargeRequest preCharge = new PreChargeRequest(
				amount
			);
			return externalBeanPayService.beforeCharge(userId, preCharge);
		}
	}
}
