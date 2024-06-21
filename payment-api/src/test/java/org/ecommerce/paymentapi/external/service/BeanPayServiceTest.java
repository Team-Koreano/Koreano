
package org.ecommerce.paymentapi.external.service;

import static java.lang.Boolean.*;
import static org.assertj.core.api.Assertions.*;
import static org.ecommerce.paymentapi.entity.enumerate.PaymentStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;
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
import org.ecommerce.paymentapi.dto.PaymentDetailMapper;
import org.ecommerce.paymentapi.dto.request.PreChargeRequest;
import org.ecommerce.paymentapi.dto.request.TossFailRequest;
import org.ecommerce.paymentapi.dto.request.TossPaymentRequest;
import org.ecommerce.paymentapi.dto.response.TossPaymentResponse;
import org.ecommerce.paymentapi.entity.ChargeInfo;
import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.SellerBeanPay;
import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.ecommerce.paymentapi.repository.PaymentDetailRepository;
import org.ecommerce.paymentapi.repository.UserBeanPayRepository;
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
	private UserBeanPayRepository beanPayRepository;

	@Mock
	private PaymentDetailRepository paymentDetailRepository;

	@Mock
	private TossServiceClient tossServiceClient;

	@Mock
	private TossKey tossKey;

	@Test
	void 사전충전객체_생성() {

		//given
		final PreChargeRequest request = new PreChargeRequest(1, 10_000);
		final UserBeanPay userBeanPay = getUserBeanPay();
		final PaymentDetail entity = userBeanPay.beforeCharge(userBeanPay.getAmount());
		final PaymentDetailDto response = PaymentDetailMapper.INSTANCE.toDto(entity);

		given(beanPayRepository.findUserBeanPayByUserId(any())).willReturn(
			userBeanPay);
		given(paymentDetailRepository.save(any())).willReturn(entity);


		//when
		final PaymentDetailDto actual = beanPayService.beforeCharge(request);

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
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String paymentType = "카드";
			final String method = "카드";
			final Integer price = 0;
			final Integer quantity = 0;
			final Integer deliveryFee = 0;
			final Integer totalAmount = 0;
			final Integer paymentAmount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";


			final TossPaymentRequest request = new TossPaymentRequest(paymentType, paymentKey,
				id, paymentAmount);
			final PaymentDetail entity = new PaymentDetail(
				id,
				new Payment(),
				getUserBeanPay(),
				getSellerBeanPay(),
				orderItemId,
				price,
				quantity,
				deliveryFee,
				totalAmount,
				paymentAmount,
				orderName,
				null,
				null,
				null,
				DEPOSIT,
				PENDING,
				List.of(),
				LocalDateTime.now(),
				null,
				TRUE
			);
			final TossPaymentResponse response = new TossPaymentResponse(paymentKey, orderName,
				method, paymentAmount, approveDateTime);
			final ResponseEntity<TossPaymentResponse> tossResponse = ResponseEntity.of(
				Optional.of(response));

			//when
			when(paymentDetailRepository.findPaymentDetailById(request.orderId())).thenReturn(entity);
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request)).thenReturn(tossResponse);

			//then
			PaymentDetailDto actual = assertDoesNotThrow(
				() -> beanPayService.validTossCharge(request, userId));

			assertEquals(actual.id(), id);
			assertEquals(actual.userId(), entity.getUserBeanPay().getUserId());
			assertEquals(actual.sellerId(), entity.getSellerBeanPay().getSellerId());
			assertEquals(actual.orderItemId(), orderItemId);
			assertEquals(actual.deliveryFee(), deliveryFee);
			assertEquals(actual.paymentAmount(), paymentAmount);
			assertEquals(actual.quantity(), quantity);
			assertEquals(actual.paymentName(), orderName);
			assertNull(actual.cancelReason());
			assertNull(actual.failReason());
			assertEquals(actual.paymentStatus(), DEPOSIT);
			assertEquals(actual.processStatus(), COMPLETED);
			assertEquals(actual.approveDateTime(),
				stringToDateTime(approveDateTime));
		}

		@Test
		void 빈페이_존재안함_예외발생() {

			//given
			final UUID orderId = UUID.randomUUID();
			final Integer userId = 1;
			final String paymentKey = "paymentKey";
			final String paymentType = "카드";
			final Integer amount = 1000;

			final TossPaymentRequest request = new TossPaymentRequest(paymentType, paymentKey,
				orderId, amount);

			//when
			when(paymentDetailRepository.findPaymentDetailById(request.orderId())).thenThrow(
				new CustomException(NOT_FOUND_ID));

			//then
			CustomException returnException = assertThrows(CustomException.class, () -> {
				beanPayService.validTossCharge(request, userId);
			});
			assertEquals(returnException.getErrorCode(), NOT_FOUND_ID);
		}

		@Test
		void 빈페이_검증값_불일치_예외발생() {
			//given
			final UUID id = UUID.randomUUID();
			final Long orderItemId = 1L;
			final Integer userId = 1;
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String paymentType = "카드";
			final String method = "카드";
			final Integer price = 0;
			final Integer quantity = 0;
			final Integer deliveryFee = 0;
			final Integer totalAmount = 0;
			final Integer paymentAmount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";
			final Integer difPaymentAmount = 10_000_000;


			final TossPaymentRequest request = new TossPaymentRequest(paymentType, paymentKey,
				id, paymentAmount);
			final PaymentDetail paymentDetail = new PaymentDetail(
				id,
				new Payment(),
				getUserBeanPay(),
				getSellerBeanPay(),
				orderItemId,
				price,
				quantity,
				deliveryFee,
				totalAmount,
				difPaymentAmount,
				orderName,
				null,
				null,
				new ChargeInfo(
					1L,
					paymentKey,
					paymentType,
					stringToDateTime(approveDateTime)
				),
				DEPOSIT,
				PENDING,
				List.of(),
				LocalDateTime.now(),
				null,
				TRUE
			);
			final TossPaymentResponse response = new TossPaymentResponse(paymentKey, orderName,
				method, paymentAmount, approveDateTime);
			final ResponseEntity<TossPaymentResponse> tossResponse = ResponseEntity.of(
				Optional.of(response));

			//when
			when(paymentDetailRepository.findPaymentDetailById(request.orderId())).thenReturn(paymentDetail);
			PaymentDetail findPaymentDetail =
				paymentDetailRepository.findPaymentDetailById(request.orderId());

			//then
			assertDoesNotThrow(() -> beanPayService.validTossCharge(request, userId));
			assertNotNull(findPaymentDetail);
			assertEquals(findPaymentDetail.getFailReason(), VERIFICATION_FAIL.getMessage());
			assertEquals(ProcessStatus.FAILED, findPaymentDetail.getProcessStatus());
		}

		@Test
		void 토스검증승인_예외발생() {
			//given
			final UUID id = UUID.randomUUID();
			final Long orderItemId = 1L;
			final Integer userId = 1;
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String paymentType = "카드";
			final String method = "카드";
			final Integer price = 0;
			final Integer quantity = 0;
			final Integer deliveryFee = 0;
			final Integer totalAmount = 0;
			final Integer paymentAmount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";


			final TossPaymentRequest request = new TossPaymentRequest(paymentType, paymentKey,
				id, paymentAmount);
			final PaymentDetail paymentDetail = new PaymentDetail(
				id,
				new Payment(),
				getUserBeanPay(),
				getSellerBeanPay(),
				orderItemId,
				price,
				quantity,
				deliveryFee,
				totalAmount,
				paymentAmount,
				orderName,
				null,
				null,
				null,
				DEPOSIT,
				PENDING,
				List.of(),
				LocalDateTime.now(),
				null,
				TRUE
			);
			final TossPaymentResponse response = new TossPaymentResponse(paymentKey, orderName,
				method, paymentAmount, approveDateTime);
			final ResponseEntity<TossPaymentResponse> tossFailResponse =
				ResponseEntity.status(400).body(response);

			//when
			when(paymentDetailRepository.findPaymentDetailById(request.orderId())).thenReturn(paymentDetail);
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request)).thenReturn(tossFailResponse);
			PaymentDetail findPaymentDetail = paymentDetailRepository.findPaymentDetailById(request.orderId());

			//then
			assertDoesNotThrow(() -> beanPayService.validTossCharge(request, userId));
			assertNotNull(findPaymentDetail);
			assertEquals(findPaymentDetail.getFailReason(),
				TOSS_RESPONSE_FAIL.getMessage());
			assertEquals(ProcessStatus.FAILED, findPaymentDetail.getProcessStatus());
		}

	}

	@Nested
	class 토스사전결제_실패 {
		@Test
		void 성공() {

			//given
			final Integer userId = 1;
			final Integer amount = 1000;
			final String errorMessage = "사용자에 의해 결제가 취소되었습니다.";
			final String errorCode = "PAY_PROCESS_CANCELED";
			final UUID id = UUID.randomUUID();
			final Long orderItemId = 1L;
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String paymentType = "카드";
			final String method = "카드";
			final Integer price = 0;
			final Integer quantity = 0;
			final Integer deliveryFee = 0;
			final Integer totalAmount = 0;
			final Integer paymentAmount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";

			final TossFailRequest request = new TossFailRequest(id, errorCode, errorMessage);


			final PaymentDetail paymentDetail = new PaymentDetail(
				id,
				new Payment(),
				getUserBeanPay(),
				getSellerBeanPay(),
				orderItemId,
				price,
				quantity,
				deliveryFee,
				totalAmount,
				paymentAmount,
				orderName,
				null,
				null,
				new ChargeInfo(
					1L,
					paymentKey,
					paymentType,
					stringToDateTime(approveDateTime)
				),
				DEPOSIT,
				PENDING,
				List.of(),
				LocalDateTime.now(),
				null,
				TRUE
			);

			//when
			when(paymentDetailRepository.findPaymentDetailById(request.orderId())).thenReturn(paymentDetail);
			PaymentDetail findPaymentDetail =
				paymentDetailRepository.findPaymentDetailById(request.orderId());

			//then
			assertDoesNotThrow(() -> beanPayService.failTossCharge(request));
			assertNotNull(findPaymentDetail);
			assertEquals(findPaymentDetail.getFailReason(), errorMessage);
			assertEquals(ProcessStatus.FAILED, findPaymentDetail.getProcessStatus());
		}
	}
	@Test
	void 빈페이_존재안함_예외발생() {
		//given
		final UUID orderId = UUID.randomUUID();
		final String errorMessage = "사용자에 의해 결제가 취소되었습니다.";
		final String errorCode = "PAY_PROCESS_CANCELED";

		final TossFailRequest request = new TossFailRequest(orderId, errorCode, errorMessage);

		//when
		when(paymentDetailRepository.findPaymentDetailById(request.orderId())).thenReturn(null);

		//then
		CustomException returnException = assertThrows(CustomException.class, () -> {
			beanPayService.failTossCharge(request);
		});
		assertEquals(returnException.getErrorCode(), NOT_FOUND_ID);
	}

	private UserBeanPay getUserBeanPay() {
		return new UserBeanPay(1, 1, 0, LocalDateTime.now(), null);
	}
	private SellerBeanPay getSellerBeanPay() {
		return new SellerBeanPay(2, 1, 0, LocalDateTime.now(), null);
	}
}