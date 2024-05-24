
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
		final PaymentDetail entity = beanPay.beforeCharge(beanPay.getAmount());
		final PaymentDetailDto response = PaymentDetailMapper.INSTANCE.entityToDto(entity);

		given(beanPayRepository.findBeanPayByUserIdAndRole(any(), any(Role.class))).willReturn(beanPay);
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
			final Role role = Role.USER;
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


			final TossDto.Request.TossPayment request = new TossDto.Request.TossPayment(paymentType, paymentKey,
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
			final TossPayment response = new TossPayment(paymentKey, orderName,
				method, paymentAmount, approveDateTime);
			final ResponseEntity<TossPayment> tossResponse = ResponseEntity.of(
				Optional.of(response));

			//when
			when(paymentDetailRepository.findPaymentDetailById(request.orderId())).thenReturn(entity);
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request)).thenReturn(tossResponse);

			//then
			PaymentDetailDto actual = assertDoesNotThrow(
				() -> beanPayService.validTossCharge(request, userId, role));

			assertEquals(actual.getId(), id);
			assertEquals(actual.getPaymentDetailId(), entity.getPayment().getId());
			assertEquals(actual.getUserId(), entity.getUserBeanPay().getUserId());
			assertEquals(actual.getSellerId(), entity.getSellerBeanPay().getUserId());
			assertEquals(actual.getOrderItemId(), orderItemId);
			assertEquals(actual.getDeliveryFee(), deliveryFee);
			assertEquals(actual.getPaymentAmount(), paymentAmount);
			assertEquals(actual.getQuantity(), quantity);
			assertEquals(actual.getPaymentName(), orderName);
			assertNull(actual.getCancelReason());
			assertNull(actual.getFailReason());
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
			when(paymentDetailRepository.findPaymentDetailById(request.orderId())).thenThrow(
				new CustomException(NOT_FOUND_ID));

			//then
			CustomException returnException = assertThrows(CustomException.class, () -> {
				beanPayService.validTossCharge(request, userId, role);
			});
			assertEquals(returnException.getErrorCode(), NOT_FOUND_ID);
		}

		@Test
		void 빈페이_검증값_불일치_예외발생() {
			//given
			final UUID id = UUID.randomUUID();
			final Long orderItemId = 1L;
			final Integer userId = 1;
			final Role role = Role.USER;
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


			final TossDto.Request.TossPayment request = new TossDto.Request.TossPayment(paymentType, paymentKey,
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
			final TossPayment response = new TossPayment(paymentKey, orderName,
				method, paymentAmount, approveDateTime);
			final ResponseEntity<TossPayment> tossResponse = ResponseEntity.of(
				Optional.of(response));

			//when
			when(paymentDetailRepository.findPaymentDetailById(request.orderId())).thenReturn(paymentDetail);
			PaymentDetail findPaymentDetail =
				paymentDetailRepository.findPaymentDetailById(request.orderId());

			//then
			assertDoesNotThrow(() -> beanPayService.validTossCharge(request, userId, role));
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
			final Role role = Role.USER;
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


			final TossDto.Request.TossPayment request = new TossDto.Request.TossPayment(paymentType, paymentKey,
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
			final TossPayment response = new TossPayment(paymentKey, orderName,
				method, paymentAmount, approveDateTime);
			final ResponseEntity<TossPayment> tossFailResponse =
				ResponseEntity.status(400).body(response);

			//when
			when(paymentDetailRepository.findPaymentDetailById(request.orderId())).thenReturn(paymentDetail);
			when(tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request)).thenReturn(tossFailResponse);
			PaymentDetail findPaymentDetail = paymentDetailRepository.findPaymentDetailById(request.orderId());

			//then
			assertDoesNotThrow(() -> beanPayService.validTossCharge(request, userId, role));
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
			final Role role = Role.USER;
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

			final TossFail request = new TossFail(id, errorCode, errorMessage);


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

		final TossFail request = new TossFail(orderId, errorCode, errorMessage);

		//when
		when(paymentDetailRepository.findPaymentDetailById(request.orderId())).thenReturn(null);

		//then
		CustomException returnException = assertThrows(CustomException.class, () -> {
			beanPayService.failTossCharge(request);
		});
		assertEquals(returnException.getErrorCode(), NOT_FOUND_ID);
	}

	private BeanPay getUserBeanPay() {
		return new BeanPay(1, 1, USER, 0, LocalDateTime.now());
	}
	private BeanPay getSellerBeanPay() {
		return new BeanPay(2, 1, SELLER, 0, LocalDateTime.now());
	}
}