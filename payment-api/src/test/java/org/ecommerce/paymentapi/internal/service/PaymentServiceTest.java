package org.ecommerce.paymentapi.internal.service;

import static java.lang.Boolean.*;
import static org.ecommerce.paymentapi.entity.enumerate.PaymentStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.Role.*;
import static org.ecommerce.paymentapi.exception.BeanPayErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PaymentCancel;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PaymentDetailPrice;
import org.ecommerce.paymentapi.dto.PaymentDto;
import org.ecommerce.paymentapi.dto.PaymentDto.Request.PaymentPrice;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.exception.PaymentDetailErrorCode;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.ecommerce.paymentapi.repository.PaymentDetailRepository;
import org.ecommerce.paymentapi.repository.PaymentRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	private static final Logger log = LoggerFactory.getLogger(PaymentServiceTest.class);
	@InjectMocks
	private PaymentService paymentService;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private PaymentDetailRepository paymentDetailRepository;

	@Mock
	private BeanPayRepository beanPayRepository;

	@Nested
	class 결제 {

		@Test
		void 성공() {
			//given
			final Long orderId = 1L;
			final Integer startAmount = 100_000;
			final Integer userId = 1;
			final BeanPay userBeanPay = new BeanPay(1, userId, USER, startAmount, LocalDateTime.now());
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Integer paymentAmount = 15000;
			final Integer[] amounts = {5000, 5000, 5000};
			final Integer[] deliveryFees = {0, 0, 0};
			final String orderName = "orderName";
			final Integer[] quantity = {3, 3, 3};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<BeanPay> sellerBeanPays = List.of(
				new BeanPay(2, 1, SELLER, 0, LocalDateTime.now()),
				new BeanPay(3, 2, SELLER, 0, LocalDateTime.now())
			);
			final PaymentPrice paymentPrice = new PaymentPrice(
				1L,
				paymentAmount,
				userBeanPay.getUserId(),
				orderName,
				List.of(
					new PaymentDetailPrice(
						orderItemIds[0],
						amounts[0],
						prices[0],
						quantity[0],
						deliveryFees[0],
						sellerIds[0],
						productNames[0]
					),
					new PaymentDetailPrice(
						orderItemIds[1],
						amounts[1],
						prices[1],
						quantity[1],
						deliveryFees[1],
						sellerIds[1],
						productNames[1]
					),
					new PaymentDetailPrice(
						orderItemIds[2],
						amounts[2],
						prices[2],
						quantity[2],
						deliveryFees[2],
						sellerIds[0],
						productNames[2]
					)
				)
			);

			final List<Pair<BeanPay, PaymentDetailPrice>> beanPayPaymentPrice = PaymentService.mappedBeanPayPaymentDetailPrice(
				paymentPrice, sellerBeanPays);
			final Payment payment = Payment.ofPayment(
				userBeanPay,
				orderId,
				paymentAmount,
				orderName,
				beanPayPaymentPrice
			);

			//when
			when(beanPayRepository.findBeanPayByUserIdAndRole(userBeanPay.getUserId(), USER))
				.thenReturn(userBeanPay);
			when(beanPayRepository.findBeanPayByUserIdsAndRole(paymentPrice.extractSellerIds()
				, SELLER)).thenReturn(sellerBeanPays);
			when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
			final PaymentDto paymentDto = paymentService.paymentPrice(paymentPrice);

			//then
			assertEquals(orderId, paymentDto.getOrderId());
			assertEquals(userBeanPay.getUserId(), paymentDto.getUserId());
			assertEquals(paymentAmount, paymentDto.getTotalAmount());
			assertEquals(paymentPrice.orderName(), paymentDto.getOrderName());
			assertEquals(COMPLETED, paymentDto.getProcessStatus());
			assertEquals(TRUE, paymentDto.getIsVisible());
			assertEquals(COMPLETED, paymentDto.getProcessStatus());
			assertEquals(startAmount - paymentAmount * 2, userBeanPay.getAmount());
			IntStream.range(0, paymentDto.getPaymentDetails().size()).forEach((i) -> {
				PaymentDetailDto dto = paymentDto.getPaymentDetails().get(i);
				PaymentDetailPrice detailPrice = paymentPrice.paymentDetails().get(i);
				assertEquals(userBeanPay.getUserId(), dto.getUserId());
				assertEquals(detailPrice.sellerId(), dto.getSellerId());
				assertEquals(detailPrice.orderItemId(), dto.getOrderItemId());
				assertEquals(detailPrice.deliveryFee(), dto.getDeliveryFee());
				assertEquals(detailPrice.paymentAmount(), dto.getPaymentAmount());
				assertEquals(detailPrice.quantity(), dto.getQuantity());
				assertEquals(detailPrice.productName(), dto.getPaymentName());
				assertEquals(PAYMENT, dto.getPaymentStatus());
				assertEquals(COMPLETED, dto.getProcessStatus());
			});
		}

		@Test
		void 결제_금액부족() {
			//given
			final Long orderId = 1L;
			final Integer paymentAmount = 15000;
			final Integer startAmount = 10_000 + paymentAmount;
			final Integer userId = 1;
			final BeanPay userBeanPay = new BeanPay(1, userId, USER, startAmount, LocalDateTime.now());
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Integer[] amounts = {5000, 5000, 5000};
			final Integer[] deliveryFees = {0, 0, 0};
			final String orderName = "orderName";
			final Integer[] quantity = {3, 3, 3};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<BeanPay> sellerBeanPays = List.of(
				new BeanPay(2, 1, SELLER, 0, LocalDateTime.now()),
				new BeanPay(3, 2, SELLER, 0, LocalDateTime.now())
			);

			final PaymentPrice paymentPrice = new PaymentPrice(
				1L,
				paymentAmount,
				userBeanPay.getUserId(),
				orderName,
				List.of(
					new PaymentDetailPrice(
						orderItemIds[0],
						amounts[0],
						prices[0],
						quantity[0],
						deliveryFees[0],
						sellerIds[0],
						productNames[0]
					),
					new PaymentDetailPrice(
						orderItemIds[1],
						amounts[1],
						prices[1],
						quantity[1],
						deliveryFees[1],
						sellerIds[1],
						productNames[1]
					),
					new PaymentDetailPrice(
						orderItemIds[2],
						amounts[2],
						prices[2],
						quantity[2],
						deliveryFees[2],
						sellerIds[0],
						productNames[2]
					)
				)
			);

			final List<Pair<BeanPay, PaymentDetailPrice>> beanPayPaymentPrice = PaymentService.mappedBeanPayPaymentDetailPrice(
				paymentPrice, sellerBeanPays);
			final Payment payment = Payment.ofPayment(
				userBeanPay,
				orderId,
				paymentAmount,
				orderName,
				beanPayPaymentPrice
			);

			//when
			when(beanPayRepository.findBeanPayByUserIdAndRole(userBeanPay.getUserId(), USER))
				.thenReturn(userBeanPay);
			when(beanPayRepository.findBeanPayByUserIdsAndRole(paymentPrice.extractSellerIds()
				, SELLER)).thenReturn(sellerBeanPays);

			//then
			CustomException actual = assertThrows(CustomException.class, () -> {
				paymentService.paymentPrice(paymentPrice);
			});
			assertEquals(actual.getErrorCode(), INSUFFICIENT_AMOUNT);
		}

		@Test
		void 판매자_존재안함() {
			//given
			final Long orderId = 1L;
			final Integer startAmount = 100_000;
			final Integer userId = 1;
			final BeanPay userBeanPay = new BeanPay(1, userId, USER, startAmount, LocalDateTime.now());
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Integer paymentAmount = 15000;
			final Integer[] amounts = {5000, 5000, 5000};
			final Integer[] deliveryFees = {0, 0, 0};
			final String orderName = "orderName";
			final Integer[] quantity = {3, 3, 3};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<BeanPay> sellerBeanPays = List.of(
				new BeanPay(2, 1, SELLER, 0, LocalDateTime.now())
			);
			final PaymentPrice paymentPrice = new PaymentPrice(
				1L,
				paymentAmount,
				userBeanPay.getUserId(),
				orderName,
				List.of(
					new PaymentDetailPrice(
						orderItemIds[0],
						amounts[0],
						prices[0],
						quantity[0],
						deliveryFees[0],
						sellerIds[0],
						productNames[0]
					),
					new PaymentDetailPrice(
						orderItemIds[1],
						amounts[1],
						prices[1],
						quantity[1],
						deliveryFees[1],
						sellerIds[1],
						productNames[1]
					),
					new PaymentDetailPrice(
						orderItemIds[2],
						amounts[2],
						prices[2],
						quantity[2],
						deliveryFees[2],
						sellerIds[0],
						productNames[2]
					)
				)
			);


			//when
			when(beanPayRepository.findBeanPayByUserIdAndRole(userBeanPay.getUserId(), USER))
				.thenReturn(userBeanPay);
			when(beanPayRepository.findBeanPayByUserIdsAndRole(paymentPrice.extractSellerIds()
				, SELLER)).thenReturn(sellerBeanPays);

			//then
			final CustomException actual = assertThrows(CustomException.class, () -> {
				paymentService.paymentPrice(paymentPrice);
			});
			assertEquals(actual.getErrorCode(), NOT_FOUND_SELLER_ID);
		}
	}

	@Test
	void 판매자빈페이_결제디테일_맵핑() {
		//given
		final Long orderId = 1L;
		final Integer startAmount = 100_000;
		final Integer userId = 1;
		final BeanPay userBeanPay = new BeanPay(1, userId, USER, startAmount, LocalDateTime.now());
		final Integer[] sellerIds = new Integer[] {1, 2};
		final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
		final Integer paymentAmount = 15000;
		final Integer[] amounts = {5000, 5000, 5000};
		final Integer[] deliveryFees = {0, 0, 0};
		final String orderName = "orderName";
		final Integer[] quantity = {3, 3, 3};
		final Integer[] prices = {1000, 1000, 1000};
		final String[] productNames = new String[]{"product1", "product2",
			"product3"};
		final List<BeanPay> sellerBeanPays = List.of(
			new BeanPay(2, 1, SELLER, 0, LocalDateTime.now()),
			new BeanPay(3, 2, SELLER, 0, LocalDateTime.now())
		);
		final PaymentPrice paymentPrice = new PaymentPrice(
			1L,
			paymentAmount,
			userBeanPay.getUserId(),
			orderName,
			List.of(
				new PaymentDetailPrice(
					orderItemIds[0],
					amounts[0],
					prices[0],
					quantity[0],
					deliveryFees[0],
					sellerIds[0],
					productNames[0]
				),
				new PaymentDetailPrice(
					orderItemIds[1],
					amounts[1],
					prices[1],
					quantity[1],
					deliveryFees[1],
					sellerIds[1],
					productNames[1]
				),
				new PaymentDetailPrice(
					orderItemIds[2],
					amounts[2],
					prices[2],
					quantity[2],
					deliveryFees[2],
					sellerIds[0],
					productNames[2]
				)
			)
		);

		//when
		final List<Pair<BeanPay, PaymentDetailPrice>> pairs = paymentService.mappedBeanPayPaymentDetailPrice(
			paymentPrice, sellerBeanPays);

		//then
		assertEquals(paymentPrice.paymentDetails().size(), pairs.size());
		for(Pair<BeanPay, PaymentDetailPrice> pair : pairs) {
			assertEquals(pair.getFirst().getUserId(), pair.getSecond().sellerId());
		}

	}


	@Nested
	class 결제_단건_취소 {
		@Test
		void 성공() {
			//given
			final Long orderId = 1L;
			final Integer startAmount = 100_000;
			final Integer userId = 1;
			final String cancelReason = "사용자 단순 변심";
			final BeanPay userBeanPay = new BeanPay(1, userId, USER, startAmount, LocalDateTime.now());
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Integer[] amounts = {5000, 5000, 5000};
			final Integer paymentAmount = Arrays.stream(amounts).mapToInt(i -> i).sum();
			final Integer[] deliveryFees = {0, 0, 0};
			final String orderName = "orderName";
			final Integer[] quantity = {3, 3, 3};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<BeanPay> sellerBeanPays = List.of(
				new BeanPay(2, 1, SELLER, 0, LocalDateTime.now()),
				new BeanPay(3, 2, SELLER, 0, LocalDateTime.now())
			);
			final PaymentPrice paymentPrice = new PaymentPrice(
				1L,
				paymentAmount,
				userBeanPay.getUserId(),
				orderName,
				List.of(
					new PaymentDetailPrice(
						orderItemIds[0],
						amounts[0],
						prices[0],
						quantity[0],
						deliveryFees[0],
						sellerIds[0],
						productNames[0]
					),
					new PaymentDetailPrice(
						orderItemIds[1],
						amounts[1],
						prices[1],
						quantity[1],
						deliveryFees[1],
						sellerIds[1],
						productNames[1]
					),
					new PaymentDetailPrice(
						orderItemIds[2],
						amounts[2],
						prices[2],
						quantity[2],
						deliveryFees[2],
						sellerIds[0],
						productNames[2]
					)
				)
			);
			final List<Pair<BeanPay, PaymentDetailPrice>> beanPayPaymentPrice = PaymentService.mappedBeanPayPaymentDetailPrice(
				paymentPrice, sellerBeanPays);
			final Payment payment = Payment.ofPayment(
				userBeanPay,
				orderId,
				paymentAmount,
				orderName,
				beanPayPaymentPrice
			);
			final PaymentDetail paymentDetail = payment.getPaymentDetails().get(0);
			final PaymentCancel request = new PaymentCancel(
				userId,
				paymentDetail.getSellerBeanPay().getUserId(),
				orderId,
				paymentDetail.getOrderItemId(),
				cancelReason
			);

			//when
			when(paymentRepository.findByOrderId(orderId)).thenReturn(payment);
			final Integer beforeSellerAmount = paymentDetail.getSellerBeanPay().getAmount();
			final PaymentDetailDto dto = paymentService.cancelPaymentDetail(request);

			//then
			assertEquals(userBeanPay.getAmount(),
				startAmount - (paymentAmount - paymentDetail.getPaymentAmount()));
			assertEquals(paymentDetail.getSellerBeanPay().getAmount(),
				beforeSellerAmount - paymentDetail.getPaymentAmount());
			assertEquals(paymentDetail.getPaymentStatus(), REFUND);
			assertEquals(paymentDetail.getProcessStatus(), CANCELLED);
			assertEquals(paymentDetail.getCancelReason(), cancelReason);
			assertEquals(paymentDetail.getPaymentStatusHistories().get(1).getPaymentStatus(),
				REFUND);
			assertEquals(paymentDetail.getPaymentStatusHistories().size(), 2);

		}

		@Test
		void 주문아이템번호_존재X() {
			//given
			final Long orderId = 1L;
			final Integer startAmount = 100_000;
			final Integer userId = 1;
			final String cancelReason = "사용자 단순 변심";
			final BeanPay userBeanPay = new BeanPay(1, userId, USER, startAmount, LocalDateTime.now());
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Long difOrderItemId = 10_000L;
			final Integer[] amounts = {5000, 5000, 5000};
			final Integer paymentAmount = Arrays.stream(amounts).mapToInt(i -> i).sum();
			final Integer[] deliveryFees = {0, 0, 0};
			final String orderName = "orderName";
			final Integer[] quantity = {3, 3, 3};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<BeanPay> sellerBeanPays = List.of(
				new BeanPay(2, 1, SELLER, 0, LocalDateTime.now()),
				new BeanPay(3, 2, SELLER, 0, LocalDateTime.now())
			);
			final PaymentPrice paymentPrice = new PaymentPrice(
				1L,
				paymentAmount,
				userBeanPay.getUserId(),
				orderName,
				List.of(
					new PaymentDetailPrice(
						orderItemIds[0],
						amounts[0],
						prices[0],
						quantity[0],
						deliveryFees[0],
						sellerIds[0],
						productNames[0]
					),
					new PaymentDetailPrice(
						orderItemIds[1],
						amounts[1],
						prices[1],
						quantity[1],
						deliveryFees[1],
						sellerIds[1],
						productNames[1]
					),
					new PaymentDetailPrice(
						orderItemIds[2],
						amounts[2],
						prices[2],
						quantity[2],
						deliveryFees[2],
						sellerIds[0],
						productNames[2]
					)
				)
			);
			final List<Pair<BeanPay, PaymentDetailPrice>> beanPayPaymentPrice = PaymentService.mappedBeanPayPaymentDetailPrice(
				paymentPrice, sellerBeanPays);
			final Payment payment = Payment.ofPayment(
				userBeanPay,
				orderId,
				paymentAmount,
				orderName,
				beanPayPaymentPrice
			);
			final PaymentDetail paymentDetail = payment.getPaymentDetails().get(0);
			final PaymentCancel request = new PaymentCancel(
				userId,
				paymentDetail.getSellerBeanPay().getUserId(),
				orderId,
				difOrderItemId,
				cancelReason
			);

			//when
			when(paymentRepository.findByOrderId(orderId)).thenReturn(payment);

			//then
			final CustomException actual = assertThrows(CustomException.class, () -> {
				paymentService.cancelPaymentDetail(request);
			});
			assertEquals(actual.getErrorCode(), PaymentDetailErrorCode.NOT_FOUND_ID);
		}

		@Test
		void 반환할_빈페이_부족() {
			//given
			final Long orderId = 1L;
			final Integer startAmount = 100_000;
			final Integer userId = 1;
			final String cancelReason = "사용자 단순 변심";
			final BeanPay userBeanPay = new BeanPay(1, userId, USER, startAmount, LocalDateTime.now());
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Integer[] amounts = {5000, 5000, 5000};
			final Integer paymentAmount = Arrays.stream(amounts).mapToInt(i -> i).sum();
			final Integer[] deliveryFees = {0, 0, 0};
			final String orderName = "orderName";
			final Integer[] quantity = {3, 3, 3};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<BeanPay> sellerBeanPays = List.of(
				new BeanPay(2, 1, SELLER, 0, LocalDateTime.now()),
				new BeanPay(3, 2, SELLER, 0, LocalDateTime.now())
			);
			final PaymentPrice paymentPrice = new PaymentPrice(
				1L,
				paymentAmount,
				userBeanPay.getUserId(),
				orderName,
				List.of(
					new PaymentDetailPrice(
						orderItemIds[0],
						amounts[0],
						prices[0],
						quantity[0],
						deliveryFees[0],
						sellerIds[0],
						productNames[0]
					),
					new PaymentDetailPrice(
						orderItemIds[1],
						amounts[1],
						prices[1],
						quantity[1],
						deliveryFees[1],
						sellerIds[1],
						productNames[1]
					)
				)
			);
			final List<Pair<BeanPay, PaymentDetailPrice>> beanPayPaymentPrice =
				PaymentService.mappedBeanPayPaymentDetailPrice(
				paymentPrice, sellerBeanPays);
			final Payment payment = Payment.ofPayment(
				userBeanPay,
				orderId,
				paymentAmount,
				orderName,
				beanPayPaymentPrice
			);
			final PaymentDetail paymentDetail = payment.getPaymentDetails().get(0);
			final PaymentCancel request = new PaymentCancel(
				userId,
				paymentDetail.getSellerBeanPay().getUserId(),
				orderId,
				paymentDetail.getOrderItemId(),
				cancelReason
			);

			//when
			when(paymentRepository.findByOrderId(orderId)).thenReturn(payment);
			paymentDetail.cancelPaymentDetail(cancelReason);

			//then
			final CustomException actual = assertThrows(CustomException.class, () -> {
				paymentService.cancelPaymentDetail(request);
			});
			assertEquals(actual.getErrorCode(), INSUFFICIENT_AMOUNT);
		}
	}


}