package org.ecommerce.paymentapi.external.service;

import static org.ecommerce.paymentapi.entity.enumerate.PaymentStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.Role.*;
import static org.ecommerce.paymentapi.exception.PaymentDetailErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.client.TossServiceClient;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PreCharge;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.TossFail;
import org.ecommerce.paymentapi.dto.TossDto;
import org.ecommerce.paymentapi.dto.TossDto.Request.TossPayment;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.ecommerce.paymentapi.internal.service.BeanPayService;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.ecommerce.paymentapi.repository.ChargeInfoRepository;
import org.ecommerce.paymentapi.repository.PaymentDetailRepository;
import org.ecommerce.paymentapi.utils.TossKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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
	private BeanPayRepository beanPayRepository;
	@Autowired
	private PaymentDetailRepository paymentDetailRepository;

	@Autowired
	private ChargeInfoRepository chargeInfoRepository;

	private BeanPay beanPay;

	@Transactional
	@BeforeEach
	public void 사전() {
		this.beanPay = BeanPay.ofCreate(1, USER);
		beanPayRepository.saveAndFlush(this.beanPay);
	}

	@AfterEach
	public void 사후() {
		chargeInfoRepository.deleteAllInBatch();
		paymentDetailRepository.deleteAllInBatch();
		beanPayRepository.deleteAllInBatch();
	}

	@Test
	void 사전객체생성() {
		//given
		final Integer amount = 10_000;
		final Integer userId = 1;
		final PreCharge request = new PreCharge(
			userId,
			amount
		);

		//when
		PaymentDetailDto actual = externalBeanPayService.beforeCharge(request);

		//then
		assertEquals(actual.getPaymentAmount(), amount);
		assertEquals(actual.getUserId(), userId);
	}

	@Nested
	class 토스검증 {

		@Test
		void 성공() {
			//사전 결제
			final Integer amount = 10_000;
			final Integer userId = 1;
			final PreCharge preCharge = new PreCharge(
				userId,
				amount
			);
			PaymentDetailDto dto = externalBeanPayService.beforeCharge(preCharge);

			//given
			final String paymentType = "paymentType";
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String approveDateTime = "2024-04-14T17:41:52+09:00";
			final UUID orderId = dto.getId();
			final TossPayment request = new TossPayment(paymentType, paymentKey,
				orderId, amount);
			final TossDto.Response.TossPayment response = new TossDto.Response.TossPayment(paymentKey, orderName,
				paymentType, amount, approveDateTime);
			final ResponseEntity<TossDto.Response.TossPayment> tossResponse =
				new ResponseEntity<>(response, HttpStatus.OK);

			//when
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request))
				.thenReturn(tossResponse);
			PaymentDetailDto actual = externalBeanPayService.validTossCharge(request, userId, USER);

			//then
			assertNotNull(actual);
			assertEquals(actual.getId(), orderId);
			assertEquals(actual.getPaymentDetailId(), dto.getPaymentDetailId());
			assertEquals(actual.getUserId(), dto.getUserId());
			assertNull(actual.getSellerId());
			assertEquals(actual.getPaymentAmount(), amount);
			assertNull(actual.getCancelReason());
			assertNull(actual.getFailReason());
			assertEquals(actual.getPaymentStatus(), DEPOSIT);
			assertEquals(actual.getProcessStatus(), COMPLETED);
			assertNotNull(actual.getApproveDateTime());
		}

		@Test
		void 중복API_호출() {
			//사전 결제
			final Integer amount = 10_000;
			final Integer userId = 1;
			final PreCharge preCharge = new PreCharge(
				userId,
				amount
			);
			PaymentDetailDto dto = externalBeanPayService.beforeCharge(preCharge);

			//given
			final String paymentType = "paymentType";
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String approveDateTime = "2024-04-14T17:41:52+09:00";
			final UUID orderId = dto.getId();
			final TossPayment request = new TossPayment(paymentType, paymentKey,
				orderId, amount);
			final TossDto.Response.TossPayment response = new TossDto.Response.TossPayment(paymentKey, orderName,
				paymentType, amount, approveDateTime);
			final ResponseEntity<TossDto.Response.TossPayment> tossResponse =
				new ResponseEntity<>(response, HttpStatus.OK);

			//when
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request))
				.thenReturn(tossResponse);
			externalBeanPayService.validTossCharge(request, userId, USER);
			PaymentDetailDto actual = externalBeanPayService.validTossCharge(request, userId,
				USER);

			// then
			assertNotNull(actual);
			assertEquals(actual.getId(), orderId);
			assertEquals(actual.getPaymentDetailId(), dto.getPaymentDetailId());
			assertEquals(actual.getUserId(), dto.getUserId());
			assertNull(actual.getSellerId());
			assertEquals(actual.getPaymentAmount(), amount);
			assertNull(actual.getCancelReason());
			assertEquals(actual.getFailReason(), DUPLICATE_API_CALL.getMessage());
			assertEquals(actual.getPaymentStatus(), DEPOSIT);
			assertEquals(actual.getProcessStatus(), FAILED);
			assertNotNull(actual.getApproveDateTime());
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
			final UUID orderId = dto.getId();
			final String errorMessage = "사용자에 의해 결제가 취소되었습니다.";
			final String errorCode = "PAY_PROCESS_CANCELED";

			final TossFail request = new TossFail(orderId, errorCode, errorMessage);

			//when
			PaymentDetailDto result = externalBeanPayService.failTossCharge(request);

			//then
			assertEquals(result.getFailReason(), errorMessage);
			assertEquals(result.getProcessStatus(), ProcessStatus.FAILED);
			assertEquals(result.getPaymentStatus(), DEPOSIT);
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

			final TossFail request = new TossFail(notExistId, errorCode, errorMessage);

			//when
			CustomException actual = assertThrows(CustomException.class, () -> {
				externalBeanPayService.failTossCharge(request);
			});
			//then
			assertEquals(actual.getErrorCode(), NOT_FOUND_ID);
		}

		private PaymentDetailDto 사전결제(Integer userId, Integer amount) {
			final PreCharge preCharge = new PreCharge(
				userId,
				amount
			);
			return externalBeanPayService.beforeCharge(preCharge);
		}
	}
}
