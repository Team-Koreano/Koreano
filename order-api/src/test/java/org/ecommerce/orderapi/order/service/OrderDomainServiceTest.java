package org.ecommerce.orderapi.order.service;

import static org.ecommerce.orderapi.order.entity.enumerated.OrderStatus.*;
import static org.ecommerce.orderapi.order.entity.enumerated.OrderStatusReason.*;
import static org.ecommerce.orderapi.order.entity.enumerated.ProductStatus.*;
import static org.ecommerce.orderapi.order.exception.OrderErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.order.client.BucketServiceClient;
import org.ecommerce.orderapi.order.client.PaymentServiceClient;
import org.ecommerce.orderapi.order.client.ProductServiceClient;
import org.ecommerce.orderapi.order.dto.OrderDtoWithOrderItemDtoList;
import org.ecommerce.orderapi.order.dto.request.CreateOrderRequest;
import org.ecommerce.orderapi.bucket.dto.response.BucketResponse;
import org.ecommerce.orderapi.order.dto.response.PaymentDetailResponse;
import org.ecommerce.orderapi.order.dto.response.PaymentResponse;
import org.ecommerce.orderapi.order.dto.response.ProductResponse;
import org.ecommerce.orderapi.order.entity.Order;
import org.ecommerce.orderapi.order.entity.OrderItem;
import org.ecommerce.orderapi.order.entity.OrderStatusHistory;
import org.ecommerce.orderapi.order.entity.PaymentDetail;
import org.ecommerce.orderapi.order.repository.OrderRepository;
import org.ecommerce.orderapi.stock.entity.Stock;
import org.ecommerce.orderapi.stock.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
public class OrderDomainServiceTest {

	@InjectMocks
	private OrderDomainService orderDomainService;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private StockRepository stockRepository;

	@Mock
	private BucketServiceClient bucketServiceClient;

	@Mock
	private ProductServiceClient productServiceClient;

	@Mock
	private PaymentServiceClient paymentServiceClient;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@Test
	void 주문_생성() {
		// given
		final Integer userId = 1;
		final CreateOrderRequest request = new CreateOrderRequest(
				List.of(1L, 2L),
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드립니다."
		);
		final List<Stock> stocks = List.of(
				new Stock(
						1,
						101,
						10,
						LocalDateTime.of(2024, 5, 4, 0, 0),
						null
				),
				new Stock(
						2,
						102,
						20,
						LocalDateTime.of(2024, 5, 4, 0, 0),
						null
				)
		);
		final List<BucketResponse> bucketServiceResponse = List.of(
				new BucketResponse(
						1L,
						1,
						"seller1",
						101,
						1,
						LocalDate.of(2024, 5, 1)
				),
				new BucketResponse(
						2L,
						1,
						"seller2",
						102,
						2,
						LocalDate.of(2024, 5, 1)
				)
		);
		final List<ProductResponse> productServiceResponse = List.of(
				new ProductResponse(
						101,
						"에디오피아 아가체프",
						1000,
						1,
						"seller1",
						AVAILABLE
				),
				new ProductResponse(
						102,
						"과테말라 안티구아",
						2000,
						2,
						"seller2",
						AVAILABLE
				)
		);
		PaymentResponse paymentServiceResponse = new PaymentResponse(
				1L,
				50000,
				LocalDateTime.of(2024, 5, 22, 0, 0),
				List.of(
						new PaymentDetailResponse(
								UUID.randomUUID(),
								1L,
								0,
								10000,
								10000,
								LocalDateTime.of(2024, 5, 22, 0, 0)
						),
						new PaymentDetailResponse(
								UUID.randomUUID(),
								2L,
								0,
								40000,
								40000,
								LocalDateTime.of(2024, 5, 22, 0, 0)
						)
				)

		);
		Order order = spy(new Order(
				1L,
				1,
				"userName",
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				null,
				0,
				OPEN,
				LocalDateTime.of(2024, 5, 22, 0, 0),
				null,
				LocalDateTime.of(2024, 5, 22, 0, 0),
				List.of(
						new OrderItem(
								1L,
								null,
								101,
								"에디오피아 아가체프",
								1000,
								10,
								null,
								null,
								null,
								1,
								"seller1",
								OPEN,
								null,
								LocalDateTime.of(2024, 5, 22, 0, 0),
								null,
								new ArrayList<>()
						),
						new OrderItem(
								2L,
								null,
								102,
								"과테말라 안티구아",
								2000,
								20,
								null,
								null,
								null,
								2,
								"seller2",
								OPEN,
								null,
								LocalDateTime.of(2024, 5, 22, 0, 0),
								null,
								new ArrayList<>()
						)
				)
		));
		Map<Long, PaymentDetail> paymentDetailMap = new HashMap<>();
		paymentDetailMap.put(1L, new PaymentDetail(
				UUID.randomUUID(),
				1L,
				0,
				10000,
				10000,
				LocalDateTime.of(2024, 5, 22, 0, 0)
		));
		paymentDetailMap.put(2L, new PaymentDetail(
				UUID.randomUUID(),
				2L,
				0,
				40000,
				40000,
				LocalDateTime.of(2024, 5, 22, 0, 0)
		));
		given(stockRepository.findByProductIdIn(anySet()))
				.willReturn(stocks);
		given(bucketServiceClient.getBuckets(anyInt(), anyList()))
				.willReturn(bucketServiceResponse);
		given(productServiceClient.getProducts(anyList()))
				.willReturn(productServiceResponse);
		given(paymentServiceClient.paymentOrder(any()))
				.willReturn(paymentServiceResponse);
		given(orderRepository.save(any()))
				.willReturn(order);
		ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
		// when
		OrderDtoWithOrderItemDtoList orderDto = orderDomainService.createOrder(userId,
				request);

		// then
		verify(orderRepository, times(1)).save(orderCaptor.capture());
		Order savedOrder = orderCaptor.getValue();
		assertEquals(userId, orderCaptor.getValue().getUserId());
		assertEquals(request.deliveryComment(), savedOrder.getDeliveryComment());
		assertEquals(request.bucketIds().size(), savedOrder.getOrderItems().size());
		assertEquals(OPEN, savedOrder.getStatus());
		assertEquals(OPEN, savedOrder.getOrderItems().get(0).getStatus());
		assertEquals(OPEN, savedOrder.getOrderItems().get(1).getStatus());
		assertEquals(APPROVE, orderDto.status());
	}

	@Test
	void 재고부족_주문생성_실패() {
		// given
		Map<Integer, Integer> productIdToQuantityMap = new HashMap<>();
		productIdToQuantityMap.put(101, 11);
		productIdToQuantityMap.put(102, 2);
		final List<Stock> stocks = List.of(
				new Stock(
						1,
						101,
						10,
						LocalDateTime.of(2024, 5, 4, 0, 0),
						null
				),
				new Stock(
						2,
						102,
						20,
						LocalDateTime.of(2024, 5, 4, 0, 0),
						null
				)
		);
		given(stockRepository.findByProductIdIn(anySet()))
				.willReturn(stocks);

		// when
		CustomException exception = assertThrows(CustomException.class,
				() -> orderDomainService.validateStock(productIdToQuantityMap));

		// then
		assertEquals(INSUFFICIENT_STOCK, exception.getErrorCode());
	}

	@Test
	void 주문_취소() {
		// given
		final Integer userId = 1;
		final Long orderId = 1L;
		final Long orderItemId = 1L;
		final LocalDateTime time = LocalDateTime.now();
		List<OrderStatusHistory> orderStatusHistories = spy(new LinkedList<>());
		orderStatusHistories.add(new OrderStatusHistory(
				1L,
				null,
				CLOSED,
				time
		));

		OrderItem orderItem = spy(new OrderItem(
				orderItemId,
				null,
				101,
				"productName1",
				10000,
				1,
				10000,
				0,
				10000,
				1,
				"sellerName1",
				CLOSED,
				null,
				time,
				time,
				orderStatusHistories
		));

		List<OrderItem> orderItems = new LinkedList<>();
		orderItems.add(orderItem);
		Order order = spy(new Order(
				1L,
				userId,
				"userName",
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드립니다.",
				10000,
				CLOSED,
				time,
				time,
				time,
				orderItems
		));

		given(orderRepository.findOrderByIdAndUserId(anyInt(), anyLong()))
				.willReturn(order);
		int previousSize = orderItem.getOrderStatusHistories().size();

		// when
		orderDomainService.cancelOrder(userId, orderId, orderItemId);

		// then
		verify(order, Mockito.times(1))
				.cancelItem(anyLong());

		assertEquals(CANCELLED, orderItem.getStatus());
		assertEquals(REFUND, orderItem.getStatusReason());
		List<OrderStatusHistory> afterOrderStatusHistories = orderItem.getOrderStatusHistories();
		assertEquals(previousSize + 1, afterOrderStatusHistories.size());
		assertEquals(CANCELLED, afterOrderStatusHistories.get(1).getChangeStatus());
	}

	@Test
	void 잘못된_주문상태_주문취소_실패() {
		// given
		final Integer userId = 1;
		final Long orderId = 1L;
		final Long orderItemId = 1L;
		final LocalDateTime time = LocalDateTime.now();
		List<OrderStatusHistory> orderStatusHistories = spy(List.of(
				new OrderStatusHistory(
						1L,
						null,
						CLOSED,
						time
				)
		));

		OrderItem orderItem = spy(new OrderItem(
				orderItemId,
				null,
				101,
				"productName1",
				10000,
				1,
				10000,
				0,
				10000,
				1,
				"sellerName1",
				APPROVE,
				null,
				time,
				time,
				orderStatusHistories
		));

		Order order = spy(new Order(
				1L,
				userId,
				"userName",
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드립니다.",
				10000,
				APPROVE,
				time,
				time,
				time,
				List.of(orderItem)
		));
		given(orderRepository.findOrderByIdAndUserId(anyInt(), anyLong()))
				.willReturn(order);
		// when
		CustomException exception = assertThrows(CustomException.class,
				() -> orderDomainService.cancelOrder(userId, orderId, orderItemId));

		// then
		assertEquals(MUST_CLOSED_ORDER_TO_CANCEL, exception.getErrorCode());
	}

	@Test
	void 기간만료_주문취소_실패() {
		// given
		final Integer userId = 1;
		final Long orderId = 1L;
		final Long orderItemId = 1L;
		final LocalDateTime time = LocalDateTime.now();
		List<OrderStatusHistory> orderStatusHistories = spy(List.of(
				new OrderStatusHistory(
						1L,
						null,
						CLOSED,
						time
				)
		));

		OrderItem orderItem = spy(new OrderItem(
				orderItemId,
				null,
				101,
				"productName1",
				10000,
				1,
				10000,
				0,
				10000,
				1,
				"sellerName1",
				CLOSED,
				null,
				LocalDateTime.of(2024, 5, 8, 0, 0),
				LocalDateTime.of(2024, 5, 8, 0, 0),
				orderStatusHistories
		));

		Order order = spy(new Order(
				1L,
				userId,
				"userName",
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드립니다.",
				10000,
				CLOSED,
				time,
				time,
				time,
				List.of(orderItem)
		));
		given(orderRepository.findOrderByIdAndUserId(anyInt(), anyLong()))
				.willReturn(order);
		// when
		CustomException exception = assertThrows(CustomException.class,
				() -> orderDomainService.cancelOrder(userId, orderId, orderItemId));

		// then
		assertEquals(TOO_OLD_ORDER_TO_CANCEL, exception.getErrorCode());
	}

}
