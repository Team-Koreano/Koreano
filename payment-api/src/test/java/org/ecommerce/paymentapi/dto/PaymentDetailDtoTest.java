package org.ecommerce.paymentapi.dto;

import static java.lang.Boolean.*;
import static org.ecommerce.paymentapi.entity.enumerate.PaymentStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.Role.*;
import static org.ecommerce.paymentapi.utils.BeanPayTimeFormatUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PaymentDetailPrice;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.TossFail;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.ChargeInfo;
import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class PaymentDetailDtoTest {

	private static ValidatorFactory factory;
	private static Validator validator;

	@BeforeAll
	public static void init() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}


	@Test
	void PaymentDetail_PaymentDetailDto_변환() {
		//given
		final UUID id = UUID.randomUUID();
		final Long orderItemId = 1L;
		final String paymentKey = "paymentKey";
		final String orderName = "orderName";
		final String paymentType = "카드";
		final Integer price = 0;
		final Integer quantity = 0;
		final Integer deliveryFee = 0;
		final Integer totalAmount = 0;
		final Integer paymentAmount = 1000;
		final String approveDateTime = "2024-04-14T17:41:52+09:00";



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

		PaymentDetailDto dto =
			PaymentDetailMapper.INSTANCE.entityToDto(entity);
		assertEquals(dto.getId(), entity.getId());
		assertEquals(dto.getPaymentDetailId(), entity.getPayment().getId());
		assertEquals(dto.getUserId(), entity.getUserBeanPay().getUserId());
		assertEquals(dto.getSellerId(), entity.getSellerBeanPay().getUserId());
		assertEquals(dto.getOrderItemId(), entity.getOrderItemId());
		assertEquals(dto.getDeliveryFee(), entity.getDeliveryFee());
		assertEquals(dto.getPaymentAmount(), entity.getPaymentAmount());
		assertEquals(dto.getCancelReason(), entity.getCancelReason());
		assertEquals(dto.getFailReason(), entity.getFailReason());
		assertEquals(dto.getProcessStatus(), entity.getProcessStatus());
		assertEquals(dto.getPaymentStatus(), entity.getPaymentStatus());
	}

	@Nested
	class 토스사전결제_실패_DTO {
		@Test
		void 성공() {
			//given
			final UUID orderId = UUID.randomUUID();
			final String errorMessage = "사용자에 의해 결제가 취소되었습니다.";
			final String errorCode = "PAY_PROCESS_CANCELED";

			//when
			final TossFail request =
				new TossFail(orderId, errorCode, errorMessage);

			//then
			assertEquals(orderId, request.orderId());
			assertEquals(errorMessage, request.errorMessage());
			assertEquals(errorCode, request.errorCode());
		}
		@Test
		void 실패() {
			//given
			final UUID orderId = null;
			final String errorMessage = "";
			final String errorCode = "";

			//when
			final PaymentDetailDto.Request.TossFail request =
				new PaymentDetailDto.Request.TossFail(orderId, errorMessage, errorCode);

			//then
			Set<ConstraintViolation<PaymentDetailDto.Request.TossFail>> violations =
				validator.validate(request);

			assertEquals(3, violations.size());
		}
	}

	@Test
	void PaymentDetailPrice_테스트() {
		//given
		PaymentDetailPrice request = new PaymentDetailPrice(
			null,
			null,
			null,
			null,
			null,
			""
		);

		//when
		Set<ConstraintViolation<PaymentDetailPrice>> violations =
			validator.validate(request);

		//then
		assertEquals(6, violations.size());
	}
	private BeanPay getUserBeanPay() {
		return new BeanPay(1, 1, USER, 0, LocalDateTime.now());
	}
	private BeanPay getSellerBeanPay() {
		return new BeanPay(2, 1, SELLER, 0, LocalDateTime.now());
	}

}