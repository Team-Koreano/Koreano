package org.ecommerce.paymentapi.external.service;

import static java.time.LocalDateTime.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.entity.ChargeInfo;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.SellerBeanPay;
import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.ecommerce.paymentapi.repository.PaymentDetailRepository;
import org.ecommerce.paymentapi.utils.PaymentTimeFormatUtil;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PaymentDetailReadServiceTest {
	@InjectMocks
	private PaymentDetailReadService paymentDetailReadService;

	@Mock
	private PaymentDetailRepository paymentDetailRepository;
	
	@Nested
	class getUserPaymentDetailsByBetweenDate {
		@Test
		void 성공() {
			//given
			final Integer userId = 999;
			final String startTime = "20240505000000";
			final String endTime = "20240606000000";
			final int size = 10;
			final int pageNum = 0;
			final PaymentStatus status = PaymentStatus.DEPOSIT;
			final Pageable pageable = PageRequest.of(pageNum, size);
			final List<PaymentDetail> paymentDetails = getUserPaymentDetails();
			final LocalDateTime startDateTime = PaymentTimeFormatUtil.stringToDateTime(startTime);
			final LocalDateTime endDateTime = PaymentTimeFormatUtil.stringToDateTime(endTime);


			//when
			when(paymentDetailRepository.findByUserIdAndBetweenCreateDateTime(
				userId, startDateTime, endDateTime, status, pageable.getPageNumber(),
				pageable.getPageSize()
			)).thenReturn(paymentDetails);

			when(paymentDetailRepository.userPaymentDetailCountByUserIdAndBetweenCreateDateTime(userId, startDateTime,
				endDateTime, status)).thenReturn((long) paymentDetails.size());

			//then
			Page<PaymentDetailDto> actual = assertDoesNotThrow(
				() -> paymentDetailReadService.getUserPaymentDetailsByBetweenDate(
					userId, startDateTime, endDateTime, status, pageable
				));
			assertNotEquals(actual.getContent().size(), 0);
			assertEquals(actual.getTotalElements(), paymentDetails.size());
			PaymentDetailDto actualDto = actual.getContent().get(0);
			PaymentDetail entity = paymentDetails.get(0);
			assertEquals(actualDto.id(), entity.getId());
			assertEquals(actualDto.userId(), entity.getUserBeanPay().getUserId());
			assertNull(actualDto.sellerId());
			assertNull(actualDto.orderItemId());
			assertEquals(actualDto.deliveryFee(), entity.getDeliveryFee());
			assertEquals(actualDto.paymentAmount(), entity.getPaymentAmount());
			assertEquals(actualDto.quantity(), entity.getQuantity());
			assertEquals(actualDto.paymentName(), entity.getPaymentName());
			assertNull(actualDto.cancelReason());
			assertNull(actualDto.failReason());
			assertEquals(actualDto.paymentKey(), entity.getChargeInfo().getPaymentKey());
			assertEquals(actualDto.payType(), entity.getChargeInfo().getPayType());
			assertEquals(actualDto.paymentStatus(), entity.getPaymentStatus());
			assertEquals(actualDto.processStatus(), entity.getProcessStatus());
			assertEquals(actualDto.createDateTime(), entity.getCreateDateTime());
			assertEquals(actualDto.updateDateTime(), entity.getUpdateDateTime());
		}
	}

	@Nested
	class getSellerPaymentDetailsByBetweenDate {
		@Test
		void 성공() {
			//given
			final Integer sellerId = 1000;
			final String startTime = "20240505000000";
			final String endTime = "20240606000000";
			final int size = 10;
			final int pageNum = 0;
			final PaymentStatus status = PaymentStatus.PAYMENT;
			final Pageable pageable = PageRequest.of(pageNum, size);
			final List<PaymentDetail> paymentDetails = getSellerPaymentDetails();
			final LocalDateTime startDateTime = PaymentTimeFormatUtil.stringToDateTime(startTime);
			final LocalDateTime endDateTime = PaymentTimeFormatUtil.stringToDateTime(endTime);


			//when
			when(paymentDetailRepository.findBySellerIdAndBetweenCreateDateTime(
				sellerId, startDateTime, endDateTime, status, pageable.getPageNumber(),
				pageable.getPageSize()
			)).thenReturn(paymentDetails);

			when(paymentDetailRepository.sellerPaymentDetailCountByUserIdAndBetweenCreatedDateTime(
				sellerId, startDateTime, endDateTime, status)).thenReturn((long) paymentDetails.size());

			//then
			Page<PaymentDetailDto> actual = assertDoesNotThrow(
				() -> paymentDetailReadService.getSellerPaymentDetailByBetweenRange(
					sellerId, startDateTime, endDateTime, status, pageable
				));

			assertNotEquals(actual.getContent().size(), 0);
			assertEquals(actual.getTotalElements(), paymentDetails.size());
			PaymentDetailDto actualDto = actual.getContent().get(0);
			PaymentDetail entity = paymentDetails.get(0);
			assertEquals(actualDto.id(), entity.getId());
			assertEquals(actualDto.userId(), entity.getUserBeanPay().getUserId());
			assertEquals(actualDto.sellerId(), entity.getSellerBeanPay().getSellerId());
			assertEquals(actualDto.orderItemId(), entity.getOrderItemId());
			assertEquals(actualDto.deliveryFee(), entity.getDeliveryFee());
			assertEquals(actualDto.paymentAmount(), entity.getPaymentAmount());
			assertEquals(actualDto.quantity(), entity.getQuantity());
			assertEquals(actualDto.paymentName(), entity.getPaymentName());
			assertNull(actualDto.cancelReason());
			assertNull(actualDto.failReason());
			assertNull(actualDto.paymentKey());
			assertNull(actualDto.payType());
			assertEquals(actualDto.paymentStatus(), entity.getPaymentStatus());
			assertEquals(actualDto.processStatus(), entity.getProcessStatus());
			assertEquals(actualDto.createDateTime(), entity.getCreateDateTime());
			assertEquals(actualDto.updateDateTime(), entity.getUpdateDateTime());
		}
	}


	private List<PaymentDetail> getUserPaymentDetails() {
		return List.of(
			new PaymentDetail(
				UUID.fromString("33316363-6565-3832-3636-633534663638"),
				null,
				getUserBeanPay(),
				null,
				null,
				0,
				0,
				0,
				10_000,
				10_000,
				"1만원 충전",
				null,
				null,
				new ChargeInfo(1L, "paymentKey", "카드", now()),
				PaymentStatus.DEPOSIT,
				ProcessStatus.COMPLETED,
				null,
				LocalDateTime.parse("2024-06-05T18:34:31.867813"),
				LocalDateTime.parse("2024-06-05T18:34:31.868924"),
				true
			),
			new PaymentDetail(
				UUID.fromString("33316363-6565-3832-3636-633534663639"),
				null,
				getUserBeanPay(),
				null,
				1L,
				0,
				0,
				0,
				10_000,
				10_000,
				"1만원 충전",
				null,
				null,
				new ChargeInfo(2L, "paymentKey", "카드", now()),
				PaymentStatus.DEPOSIT,
				ProcessStatus.COMPLETED,
				null,
				LocalDateTime.parse("2024-06-05T18:34:31.867813"),
				LocalDateTime.parse("2024-06-05T18:34:31.868924"),
				true
			),
			new PaymentDetail(
				UUID.fromString("33316363-6565-3832-3636-633534663640"),
				null,
				getUserBeanPay(),
				null,
				2L,
				0,
				0,
				0,
				10_000,
				10_000,
				"1만원 충전",
				null,
				null,
				new ChargeInfo(3L, "paymentKey", "카드", now()),
				PaymentStatus.DEPOSIT,
				ProcessStatus.COMPLETED,
				null,
				LocalDateTime.parse("2024-06-05T18:34:31.867813"),
				LocalDateTime.parse("2024-06-05T18:34:31.868924"),
				true
			)
		);
	}

	private List<PaymentDetail> getSellerPaymentDetails() {
		return List.of(
			new PaymentDetail(
				UUID.fromString("33316363-6565-3832-3636-633534663638"),
				null,
				getUserBeanPay(),
				getSellerBeanPay(),
				3L,
				5000,
				4,
				0,
				20_000,
				20_000,
				"코맥 무표맥 커피여과지 #2, 100매입, 4개",
				null,
				null,
				null,
				PaymentStatus.PAYMENT,
				ProcessStatus.COMPLETED,
				null,
				LocalDateTime.parse("2024-06-05T18:34:31.867813"),
				LocalDateTime.parse("2024-06-05T18:34:31.868924"),
				true
			),
			new PaymentDetail(
				UUID.fromString("33316363-6565-3832-3636-633534663639"),
				null,
				getUserBeanPay(),
				getSellerBeanPay(),
				null,
				2_000,
				5,
				0,
				10_000,
				10_000,
				"워너빈 시그니쳐 블랜디드 원두커피, 1kg, 홀빈(분쇄안함), 5개",
				null,
				null,
				null,
				PaymentStatus.PAYMENT,
				ProcessStatus.COMPLETED,
				null,
				LocalDateTime.parse("2024-06-05T18:34:31.867813"),
				LocalDateTime.parse("2024-06-05T18:34:31.868924"),
				true
			),
			new PaymentDetail(
				UUID.fromString("33316363-6565-3832-3636-633534663640"),
				null,
				getUserBeanPay(),
				null,
				null,
				10_000,
				3,
				0,
				30_000,
				30_000,
				"곰곰 콜롬비아 블렌드 원두, 홀빈(분쇄안함), 250g, 3개",
				null,
				null,
				null,
				PaymentStatus.PAYMENT,
				ProcessStatus.COMPLETED,
				null,
				LocalDateTime.parse("2024-06-05T18:34:31.867813"),
				LocalDateTime.parse("2024-06-05T18:34:31.868924"),
				true
			)
		);
	}

	private UserBeanPay getUserBeanPay() {
		return new UserBeanPay(1, 999, 0, now(), null);
	}
	private SellerBeanPay getSellerBeanPay() {
		return new SellerBeanPay(2, 1000, 0, now(), null);
	}
}