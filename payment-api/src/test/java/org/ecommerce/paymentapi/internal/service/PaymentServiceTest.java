package org.ecommerce.paymentapi.internal.service;

import static java.lang.Boolean.*;
import static org.ecommerce.paymentapi.entity.enumerate.PaymentStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;
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
import org.ecommerce.paymentapi.dto.PaymentDtoWithDetail;
import org.ecommerce.paymentapi.dto.request.PaymentCancelRequest;
import org.ecommerce.paymentapi.dto.request.PaymentDetailPriceRequest;
import org.ecommerce.paymentapi.dto.request.PaymentPriceRequest;
import org.ecommerce.paymentapi.entity.SellerBeanPay;
import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.exception.PaymentDetailErrorCode;
import org.ecommerce.paymentapi.repository.SellerBeanPayRepository;
import org.ecommerce.paymentapi.repository.UserBeanPayRepository;
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
	private UserBeanPayRepository userBeanPayRepository;

	@Mock
	private SellerBeanPayRepository sellerBeanPayRepository;

	@Nested
	class 결제 {

		@Test
		void 성공() {
			//given
			final Long orderId = 1L;
			final Integer startAmount = 100_000;
			final Integer userId = 1;
			final UserBeanPay userBeanPay = new UserBeanPay(1, userId, startAmount, LocalDateTime.now(), null);
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Integer paymentAmount = 15000;
			final Integer[] paymentAmounts = {5000, 5000, 5000};
			final Integer[] deliveryFees = {0, 0, 0};
			final String orderName = "orderName";
			final Integer[] quantity = {5, 5, 5};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<SellerBeanPay> sellerUserBeanPays = List.of(
				new SellerBeanPay(2, 1, 0, LocalDateTime.now(), null),
				new SellerBeanPay(3, 2, 0, LocalDateTime.now(), null)
			);
			final PaymentPriceRequest paymentPrice = new PaymentPriceRequest(
				1L,
				userBeanPay.getUserId(),
				orderName,
				List.of(
					new PaymentDetailPriceRequest(
						orderItemIds[0],
						prices[0],
						quantity[0],
						deliveryFees[0],
						sellerIds[0],
						productNames[0]
					),
					new PaymentDetailPriceRequest(
						orderItemIds[1],
						prices[1],
						quantity[1],
						deliveryFees[1],
						sellerIds[1],
						productNames[1]
					),
					new PaymentDetailPriceRequest(
						orderItemIds[2],
						prices[2],
						quantity[2],
						deliveryFees[2],
						sellerIds[0],
						productNames[2]
					)
				)
			);

			final List<Pair<SellerBeanPay, PaymentDetailPriceRequest>> beanPayPaymentPrice =
				PaymentService.mappedBeanPayPaymentDetailPrice(
				paymentPrice, sellerUserBeanPays);
			final Payment payment = Payment.ofPayment(
				userBeanPay,
				orderId,
				orderName,
				beanPayPaymentPrice
			);

			//when
			when(userBeanPayRepository.findUserBeanPayByUserId(userBeanPay.getUserId()))
				.thenReturn(userBeanPay);
			when(sellerBeanPayRepository.findSellerBeanPayBySellerIds(paymentPrice.extractSellerIds()
				)).thenReturn(sellerUserBeanPays);
			when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
			final PaymentDtoWithDetail paymentDto = paymentService.paymentPrice(paymentPrice);

			//then
			assertEquals(orderId, paymentDto.orderId());
			assertEquals(userBeanPay.getUserId(), paymentDto.userId());
			assertEquals(paymentAmount, paymentDto.totalPaymentAmount());
			assertEquals(paymentPrice.orderName(), paymentDto.orderName());
			assertEquals(COMPLETED, paymentDto.processStatus());
			assertEquals(TRUE, paymentDto.isVisible());
			assertEquals(COMPLETED, paymentDto.processStatus());
			assertEquals(startAmount - paymentAmount * 2, userBeanPay.getAmount());
			IntStream.range(0, paymentDto.paymentDetailDtos().size()).forEach((i) -> {
				PaymentDetailDto dto = paymentDto.paymentDetailDtos().get(i);
				PaymentDetailPriceRequest detailPrice = paymentPrice.paymentDetails().get(i);
				assertEquals(userBeanPay.getUserId(), dto.userId());
				assertEquals(detailPrice.sellerId(), dto.sellerId());
				assertEquals(detailPrice.orderItemId(), dto.orderItemId());
				assertEquals(detailPrice.deliveryFee(), dto.deliveryFee());
				assertEquals(detailPrice.quantity(), dto.quantity());
				assertEquals(detailPrice.productName(), dto.paymentName());
				assertEquals(PAYMENT, dto.paymentStatus());
				assertEquals(COMPLETED, dto.processStatus());
			});
		}

		@Test
		void 결제_금액부족() {
			//given
			final Long orderId = 1L;
			final Integer paymentAmount = 15000;
			final Integer startAmount = 10_000 + paymentAmount;
			final Integer userId = 1;
			final UserBeanPay userBeanPay = new UserBeanPay(1, userId, startAmount, LocalDateTime.now(), null);
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Integer[] paymentAmounts = {5000, 5000, 5000};
			final Integer[] deliveryFees = {0, 0, 0};
			final String orderName = "orderName";
			final Integer[] quantity = {5, 5, 5};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<SellerBeanPay> sellerUserBeanPays = List.of(
				new SellerBeanPay(2, 1, 0, LocalDateTime.now(), null),
				new SellerBeanPay(3, 2, 0, LocalDateTime.now(), null)
			);

			final PaymentPriceRequest paymentPrice = new PaymentPriceRequest(
				1L,
				userId,
				orderName,
				List.of(
					new PaymentDetailPriceRequest(
						orderItemIds[0],
						prices[0],
						quantity[0],
						deliveryFees[0],
						sellerIds[0],
						productNames[0]
					),
					new PaymentDetailPriceRequest(
						orderItemIds[1],
						prices[1],
						quantity[1],
						deliveryFees[1],
						sellerIds[1],
						productNames[1]
					),
					new PaymentDetailPriceRequest(
						orderItemIds[2],
						prices[2],
						quantity[2],
						deliveryFees[2],
						sellerIds[0],
						productNames[2]
					)
				)
			);

			final List<Pair<SellerBeanPay, PaymentDetailPriceRequest>> beanPayPaymentPrice =
				PaymentService.mappedBeanPayPaymentDetailPrice(
				paymentPrice, sellerUserBeanPays);
			final Payment payment = Payment.ofPayment(
				userBeanPay,
				orderId,
				orderName,
				beanPayPaymentPrice
			);

			//when
			when(userBeanPayRepository.findUserBeanPayByUserId(userBeanPay.getUserId()))
				.thenReturn(userBeanPay);
			when(sellerBeanPayRepository.findSellerBeanPayBySellerIds(paymentPrice.extractSellerIds()
				)).thenReturn(sellerUserBeanPays);

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
			final UserBeanPay userBeanPay = new UserBeanPay(1, userId, startAmount, LocalDateTime.now(), null);
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Integer paymentAmount = 15000;
			final Integer[] paymentAmounts = {5000, 5000, 5000};
			final Integer[] deliveryFees = {0, 0, 0};
			final String orderName = "orderName";
			final Integer[] quantity = {3, 3, 3};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<SellerBeanPay> sellerUserBeanPays = List.of(
				new SellerBeanPay(2, 1, 0, LocalDateTime.now(), null)
			);
			final PaymentPriceRequest paymentPrice = new PaymentPriceRequest(
				1L,
				userId,
				orderName,
				List.of(
					new PaymentDetailPriceRequest(
						orderItemIds[0],
						prices[0],
						quantity[0],
						deliveryFees[0],
						sellerIds[0],
						productNames[0]
					),
					new PaymentDetailPriceRequest(
						orderItemIds[1],
						prices[1],
						quantity[1],
						deliveryFees[1],
						sellerIds[1],
						productNames[1]
					),
					new PaymentDetailPriceRequest(
						orderItemIds[2],
						prices[2],
						quantity[2],
						deliveryFees[2],
						sellerIds[0],
						productNames[2]
					)
				)
			);


			//when
			when(userBeanPayRepository.findUserBeanPayByUserId(userBeanPay.getUserId()))
				.thenReturn(userBeanPay);
			when(sellerBeanPayRepository.findSellerBeanPayBySellerIds(paymentPrice.extractSellerIds()
				)).thenReturn(sellerUserBeanPays);

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
		final UserBeanPay userBeanPay = new UserBeanPay(1, userId, startAmount, LocalDateTime.now(), null);
		final Integer[] sellerIds = new Integer[] {1, 2};
		final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
		final Integer paymentAmount = 15000;
		final Integer[] paymentAmounts = {5000, 5000, 5000};
		final Integer[] deliveryFees = {0, 0, 0};
		final String orderName = "orderName";
		final Integer[] quantity = {3, 3, 3};
		final Integer[] prices = {1000, 1000, 1000};
		final String[] productNames = new String[]{"product1", "product2",
			"product3"};
		final List<SellerBeanPay> sellerUserBeanPays = List.of(
			new SellerBeanPay(2, 1, 0, LocalDateTime.now(), null),
			new SellerBeanPay(3, 2, 0, LocalDateTime.now(), null)
		);
		final PaymentPriceRequest paymentPrice = new PaymentPriceRequest(
			1L,
			userId,
			orderName,
			List.of(
				new PaymentDetailPriceRequest(
					orderItemIds[0],
					prices[0],
					quantity[0],
					deliveryFees[0],
					sellerIds[0],
					productNames[0]
				),
				new PaymentDetailPriceRequest(
					orderItemIds[1],
					prices[1],
					quantity[1],
					deliveryFees[1],
					sellerIds[1],
					productNames[1]
				),
				new PaymentDetailPriceRequest(
					orderItemIds[2],
					prices[2],
					quantity[2],
					deliveryFees[2],
					sellerIds[0],
					productNames[2]
				)
			)
		);

		//when
		final List<Pair<SellerBeanPay, PaymentDetailPriceRequest>> pairs =
			paymentService.mappedBeanPayPaymentDetailPrice(
			paymentPrice, sellerUserBeanPays);

		//then
		assertEquals(paymentPrice.paymentDetails().size(), pairs.size());
		for(Pair<SellerBeanPay, PaymentDetailPriceRequest> pair : pairs) {
			assertEquals(pair.getFirst().getSellerId(), pair.getSecond().sellerId());
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
			final UserBeanPay userBeanPay = new UserBeanPay(1, userId, startAmount, LocalDateTime.now(), null);
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Integer[] paymentAmounts = {5000, 5000, 5000};
			final Integer totalPaymentAmount = Arrays.stream(paymentAmounts).mapToInt(i -> i).sum();
			final Integer[] deliveryFees = {2000, 2000, 2000};
			final String orderName = "orderName";
			final Integer[] quantity = {3, 3, 3};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<SellerBeanPay> sellerUserBeanPays = List.of(
				new SellerBeanPay(2, 1, 0, LocalDateTime.now(), null),
				new SellerBeanPay(3, 2, 0, LocalDateTime.now(), null)
			);
			final PaymentPriceRequest paymentPrice = new PaymentPriceRequest(
				1L,
				userId,
				orderName,
				List.of(
					new PaymentDetailPriceRequest(
						orderItemIds[0],
						prices[0],
						quantity[0],
						deliveryFees[0],
						sellerIds[0],
						productNames[0]
					),
					new PaymentDetailPriceRequest(
						orderItemIds[1],
						prices[1],
						quantity[1],
						deliveryFees[1],
						sellerIds[1],
						productNames[1]
					),
					new PaymentDetailPriceRequest(
						orderItemIds[2],
						prices[2],
						quantity[2],
						deliveryFees[2],
						sellerIds[0],
						productNames[2]
					)
				)
			);
			final List<Pair<SellerBeanPay, PaymentDetailPriceRequest>> beanPayPaymentPrice =
				PaymentService.mappedBeanPayPaymentDetailPrice(
				paymentPrice, sellerUserBeanPays);
			final Payment payment = Payment.ofPayment(
				userBeanPay,
				orderId,
				orderName,
				beanPayPaymentPrice
			);
			final PaymentDetail paymentDetail = payment.getPaymentDetails().get(0);
			final PaymentCancelRequest request = new PaymentCancelRequest(
				userId,
				paymentDetail.getSellerBeanPay().getSellerId(),
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
				startAmount - (totalPaymentAmount - paymentDetail.getPaymentAmount()));
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
			final UserBeanPay userBeanPay = new UserBeanPay(1, userId, startAmount, LocalDateTime.now(), null);
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Long difOrderItemId = 10_000L;
			final Integer[] paymentAmounts = {5000, 5000, 5000};
			final Integer paymentAmount = Arrays.stream(paymentAmounts).mapToInt(i -> i).sum();
			final Integer[] deliveryFees = {0, 0, 0};
			final String orderName = "orderName";
			final Integer[] quantity = {3, 3, 3};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<SellerBeanPay> sellerUserBeanPays = List.of(
				new SellerBeanPay(2, 1, 0, LocalDateTime.now(), null),
				new SellerBeanPay(3, 2, 0, LocalDateTime.now(), null)
			);
			final PaymentPriceRequest paymentPrice = new PaymentPriceRequest(
				1L,
				userId,
				orderName,
				List.of(
					new PaymentDetailPriceRequest(
						orderItemIds[0],
						prices[0],
						quantity[0],
						deliveryFees[0],
						sellerIds[0],
						productNames[0]
					),
					new PaymentDetailPriceRequest(
						orderItemIds[1],
						prices[1],
						quantity[1],
						deliveryFees[1],
						sellerIds[1],
						productNames[1]
					),
					new PaymentDetailPriceRequest(
						orderItemIds[2],
						prices[2],
						quantity[2],
						deliveryFees[2],
						sellerIds[0],
						productNames[2]
					)
				)
			);
			final List<Pair<SellerBeanPay, PaymentDetailPriceRequest>> beanPayPaymentPrice = PaymentService.mappedBeanPayPaymentDetailPrice(
				paymentPrice, sellerUserBeanPays);
			final Payment payment = Payment.ofPayment(
				userBeanPay,
				orderId,
				orderName,
				beanPayPaymentPrice
			);
			final PaymentDetail paymentDetail = payment.getPaymentDetails().get(0);
			final PaymentCancelRequest request = new PaymentCancelRequest(
				userId,
				paymentDetail.getSellerBeanPay().getSellerId(),
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
			final UserBeanPay userBeanPay = new UserBeanPay(1, userId, startAmount, LocalDateTime.now(), null);
			final Integer[] sellerIds = new Integer[] {1, 2};
			final Long[] orderItemIds = new Long[] {1L, 2L, 3L};
			final Integer[] paymentAmounts = {5000, 5000, 5000};
			final Integer paymentAmount = Arrays.stream(paymentAmounts).mapToInt(i -> i).sum();
			final Integer[] deliveryFees = {0, 0, 0};
			final String orderName = "orderName";
			final Integer[] quantity = {3, 3, 3};
			final Integer[] prices = {1000, 1000, 1000};
			final String[] productNames = new String[]{"product1", "product2",
				"product3"};
			final List<SellerBeanPay> sellerUserBeanPays = List.of(
				new SellerBeanPay(2, 1, 0, LocalDateTime.now(), null),
				new SellerBeanPay(3, 2, 0, LocalDateTime.now(), null)
			);
			final PaymentPriceRequest paymentPrice = new PaymentPriceRequest(
				1L,
				userId,
				orderName,
				List.of(
					new PaymentDetailPriceRequest(
						orderItemIds[0],
						prices[0],
						quantity[0],
						deliveryFees[0],
						sellerIds[0],
						productNames[0]
					),
					new PaymentDetailPriceRequest(
						orderItemIds[1],
						prices[1],
						quantity[1],
						deliveryFees[1],
						sellerIds[1],
						productNames[1]
					)
				)
			);
			final List<Pair<SellerBeanPay, PaymentDetailPriceRequest>> beanPayPaymentPrice =
				PaymentService.mappedBeanPayPaymentDetailPrice(
				paymentPrice, sellerUserBeanPays);
			final Payment payment = Payment.ofPayment(
				userBeanPay,
				orderId,
				orderName,
				beanPayPaymentPrice
			);
			final PaymentDetail paymentDetail = payment.getPaymentDetails().get(0);
			final PaymentCancelRequest request = new PaymentCancelRequest(
				userId,
				paymentDetail.getSellerBeanPay().getSellerId(),
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