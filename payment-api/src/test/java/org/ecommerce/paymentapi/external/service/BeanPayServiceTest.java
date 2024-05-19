
package org.ecommerce.paymentapi.external.service;

import static java.lang.Boolean.*;
import static org.assertj.core.api.Assertions.*;
import static org.ecommerce.paymentapi.entity.enumerate.PaymentStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.Role.*;
import static org.ecommerce.paymentapi.exception.PaymentDetailErrorCode.*;
import static org.ecommerce.paymentapi.utils.BeanPayTimeFormatUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.client.TossServiceClient;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PreCharge;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.TossFail;
import org.ecommerce.paymentapi.dto.PaymentDetailMapper;
import org.ecommerce.paymentapi.dto.TossDto;
import org.ecommerce.paymentapi.dto.TossDto.Response.TossPayment;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.ChargeInfo;
import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.ecommerce.paymentapi.entity.enumerate.Role;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.ecommerce.paymentapi.repository.PaymentDetailRepository;
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
	private PaymentDetailRepository paymentDetailRepository;

	@Mock
	private TossServiceClient tossServiceClient;

	@Mock
	private TossKey tossKey;

	@Test
	void 사전충전객체_생성() {

		//given
		final PreCharge request = new PreCharge(1, 10_000);
		final BeanPay beanPay = getUserBeanPay();
		final PaymentDetail entity = beanPay.preCharge(beanPay.getAmount());
		final PaymentDetailDto response = PaymentDetailMapper.INSTANCE.toDto(entity);

		given(beanPayRepository.findBeanPayByUserIdAndRole(any(), any(Role.class))).willReturn(Optional.of(beanPay));
		given(paymentDetailRepository.save(any())).willReturn(entity);


		//when
		final PaymentDetailDto actual = beanPayService.preCharge(request);

		//then
		assertThat(actual).usingRecursiveComparison().isEqualTo(response);
	}

	@Nested
	class 토스결제_검증 {
		@Test
		void 성공() {
			//given
			final UUID id = UUID.randomUUID();
			final Long orderItemId = 1L;
			final Integer userId = 1;
			final Role role = Role.USER;
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String paymentType = "카드";
			final String method = "카드";
			final Integer amount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";
			final Integer quantity = 0;
			final Integer deliveryFee = 0;


			final TossDto.Request.TossPayment request = new TossDto.Request.TossPayment(paymentType, paymentKey,
				id, amount);
			final PaymentDetail entity = new PaymentDetail(
				id,
				new Payment(),
				getUserBeanPay(),
				getSellerBeanPay(),
				orderItemId,
				deliveryFee,
				amount,
				quantity,
				"paymentName",
				null,
				null,
				new ChargeInfo(
					1L,
					null,
					"paymentKey",
					"payType",
					LocalDateTime.now()
				),
				DEPOSIT,
				PENDING,
				List.of(),
				LocalDateTime.now(),
				null,
				TRUE
			);
			final TossPayment response = new TossPayment(paymentKey, orderName,
				method, amount, approveDateTime);
			final ResponseEntity<TossPayment> tossResponse = ResponseEntity.of(
				Optional.of(response));

			//when
			when(paymentDetailRepository.findById(request.orderId())).thenReturn(Optional.of(entity));
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request)).thenReturn(tossResponse);

			//then
			PaymentDetailDto actual = assertDoesNotThrow(
				() -> beanPayService.validTossCharge(request, userId, role));
			assertEquals(actual.getId(), id);
			assertEquals(actual.getPaymentId(), entity.getPayment().getId());
			assertEquals(actual.getUserId(), entity.getUserBeanPay().getUserId());
			assertEquals(actual.getSellerId(), entity.getSellerBeanPay().getUserId());
			assertEquals(actual.getOrderItemId(), orderItemId);
			assertEquals(actual.getDeliveryFee(), deliveryFee);
			assertEquals(actual.getPaymentAmount(), amount);
			assertEquals(actual.getQuantity(), quantity);
			assertEquals(actual.getPaymentName(), orderName);
			assertEquals(actual.getCancelReason(), null);
			assertEquals(actual.getFailReason(), null);
			assertEquals(actual.getPaymentStatus(), DEPOSIT);
			assertEquals(actual.getProcessStatus(), COMPLETED);
			assertEquals(actual.getApproveDateTime(),
				stringToDateTime(approveDateTime));
		}

		@Test
		void 빈페이_존재안함_예외발생() {

			//given
			final UUID orderId = UUID.randomUUID();
			final Integer userId = 1;
			final Role role = Role.USER;
			final String paymentKey = "paymentKey";
			final String paymentType = "카드";
			final Integer amount = 1000;

			final TossDto.Request.TossPayment request = new TossDto.Request.TossPayment(paymentType, paymentKey,
				orderId, amount);

			//when
			when(paymentDetailRepository.findById(request.orderId())).thenThrow(
				new CustomException(NOT_EXIST));

			//then
			CustomException returnException = assertThrows(CustomException.class, () -> {
				beanPayService.validTossCharge(request, userId, role);
			});
			assertEquals(returnException.getErrorCode(), NOT_EXIST);
		}

		@Test
		void 빈페이_검증값_불일치_예외발생() {
			//given
			final UUID orderId = UUID.randomUUID();
			final Integer userId = 1;
			final Role role = Role.USER;
			final String paymentKey = "paymentKey";
			final String paymentType = "카드";
			final Integer amount = 1000;
			final Integer difAmount = 10000;

			final TossDto.Request.TossPayment request = new TossDto.Request.TossPayment(paymentType, paymentKey,
				orderId, amount);
			final PaymentDetail paymentDetail = new PaymentDetail(
				orderId,
				new Payment(),
				getUserBeanPay(),
				getSellerBeanPay(),
				1L,
				0,
				difAmount,
				0,
				"paymentName",
				null,
				null,
				new ChargeInfo(
					1L,
					null,
					"paymentKey",
					"payType",
					LocalDateTime.now()
				),
				PaymentStatus.PAYMENT,
				PENDING,
				List.of(),
				LocalDateTime.now(),
				null,
				TRUE
			);

			//when
			when(paymentDetailRepository.findById(request.orderId())).thenReturn(Optional.of(paymentDetail));
			Optional<PaymentDetail> optionalBeanPay =
				paymentDetailRepository.findById(request.orderId());

			//then
			assertDoesNotThrow(() -> beanPayService.validTossCharge(request, userId, role));
			assertTrue(optionalBeanPay.isPresent());
			assertEquals(optionalBeanPay.get().getFailReason(), VERIFICATION_FAIL.getMessage());
			assertEquals(ProcessStatus.FAILED, optionalBeanPay.get().getProcessStatus());
		}

		@Test
		void 토스검증승인_예외발생() {
			//given
			final UUID orderId = UUID.randomUUID();
			final Integer userId = 1;
			final Role role = Role.USER;
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String paymentType = "카드";
			final String method = "카드";
			final Integer amount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";

			final TossDto.Request.TossPayment request = new TossDto.Request.TossPayment(paymentType, paymentKey,
				orderId, amount);
			final PaymentDetail paymentDetail = new PaymentDetail(
				orderId,
				new Payment(),
				getUserBeanPay(),
				getSellerBeanPay(),
				1L,
				0,
				amount,
				0,
				"paymentName",
				null,
				null,
				new ChargeInfo(
					1L,
					null,
					"paymentKey",
					"payType",
					LocalDateTime.now()
				),
				PaymentStatus.PAYMENT,
				PENDING,
				List.of(),
				LocalDateTime.now(),
				null,
				TRUE
			);
			final TossPayment response = new TossPayment(paymentType, orderName,
				method, amount, approveDateTime);
			final ResponseEntity<TossPayment> tossFailResponse = ResponseEntity.status(400)
				.body(response);

			//when
			when(paymentDetailRepository.findById(request.orderId())).thenReturn(Optional.of(paymentDetail));
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request)).thenReturn(tossFailResponse);
			Optional<PaymentDetail> optionalPaymentDetail =
				paymentDetailRepository.findById(request.orderId());

			//then
			assertDoesNotThrow(() -> beanPayService.validTossCharge(request, userId, role));
			assertTrue(optionalPaymentDetail.isPresent());
			assertEquals(optionalPaymentDetail.get().getFailReason(),
				TOSS_RESPONSE_FAIL.getMessage());
			assertEquals(ProcessStatus.FAILED, optionalPaymentDetail.get().getProcessStatus());
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

			final TossFail request = new TossFail(orderId, errorCode, errorMessage);

			final PaymentDetail paymentDetail = new PaymentDetail(
				UUID.randomUUID(),
				new Payment(),
				getUserBeanPay(),
				getSellerBeanPay(),
				1L,
				0,
				0,
				0,
				"paymentName",
				null,
				null,
				new ChargeInfo(
					1L,
					null,
					"paymentKey",
					"payType",
					LocalDateTime.now()
				),
				PaymentStatus.PAYMENT,
				PENDING,
				List.of(),
				LocalDateTime.now(),
				null,
				TRUE
			);

			//when
			when(paymentDetailRepository.findById(request.orderId())).thenReturn(Optional.of(paymentDetail));
			Optional<PaymentDetail> optionalPaymentDetail =
				paymentDetailRepository.findById(request.orderId());

			//then
			assertDoesNotThrow(() -> beanPayService.failTossCharge(request));
			assertTrue(optionalPaymentDetail.isPresent());
			assertEquals(optionalPaymentDetail.get().getFailReason(), errorMessage);
			assertEquals(ProcessStatus.FAILED, optionalPaymentDetail.get().getProcessStatus());
		}
	}
	@Test
	void 빈페이_존재안함_예외발생() {
		//given
		final UUID orderId = UUID.randomUUID();
		final String errorMessage = "사용자에 의해 결제가 취소되었습니다.";
		final String errorCode = "PAY_PROCESS_CANCELED";

		final TossFail request = new TossFail(orderId, errorCode, errorMessage);

		//when
		when(paymentDetailRepository.findById(request.orderId())).thenThrow(
			new CustomException(NOT_EXIST));

		//then
		CustomException returnException = assertThrows(CustomException.class, () -> {
			beanPayService.failTossCharge(request);
		});
		assertEquals(returnException.getErrorCode(), NOT_EXIST);
	}

	private BeanPay getUserBeanPay() {
		return new BeanPay(1, 1, USER, 0, LocalDateTime.now());
	}
	private BeanPay getSellerBeanPay() {
		return new BeanPay(2, 1, SELLER, 0, LocalDateTime.now());
	}
}