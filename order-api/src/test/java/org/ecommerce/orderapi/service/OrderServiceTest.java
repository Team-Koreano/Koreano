package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.entity.enumerated.ProductStatus.*;
import static org.ecommerce.orderapi.exception.OrderErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.client.BucketServiceClient;
import org.ecommerce.orderapi.client.ProductServiceClient;
import org.ecommerce.orderapi.dto.BucketDto;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.dto.ProductDto;
import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.OrderDetail;
import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.entity.enumerated.OrderStatus;
import org.ecommerce.orderapi.repository.OrderRepository;
import org.ecommerce.orderapi.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

	@InjectMocks
	private OrderService orderService;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private StockRepository stockRepository;

	@Mock
	private BucketServiceClient bucketServiceClient;

	@Mock
	private ProductServiceClient productServiceClient;

	@Test
	void 주문_생성() {
		// given
		final Integer userId = 1;
		final OrderDto.Request.Place request = new OrderDto.Request.Place(
				List.of(1L, 2L),
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드립니다."
		);
		final List<Product> products = List.of(
				new Product(
						101,
						"상품 이름1",
						1000,
						"seller1",
						AVAILABLE
				),
				new Product(
						102,
						"상품 이름2",
						2000,
						"seller2",
						AVAILABLE
				)
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
		final List<BucketDto.Response> bucketServiceResponse = List.of(
				new BucketDto.Response(
						1L,
						1,
						"seller1",
						101,
						1,
						LocalDate.of(2024, 5, 1)
				),
				new BucketDto.Response(
						2L,
						1,
						"seller2",
						102,
						2,
						LocalDate.of(2024, 5, 1)
				)
		);
		final List<ProductDto.Response> productServiceResponse = List.of(
				new ProductDto.Response(
						101,
						"에디오피아 아가체프",
						1000,
						"seller1",
						AVAILABLE
				),
				new ProductDto.Response(
						102,
						"과테말라 안티구아",
						2000,
						"seller2",
						AVAILABLE
				)
		);
		given(stockRepository.findByProductIdIn(anyList()))
				.willReturn(stocks);
		given(bucketServiceClient.getBuckets(anyInt(), anyList()))
				.willReturn(bucketServiceResponse);
		given(productServiceClient.getProducts(anyList()))
				.willReturn(productServiceResponse);
		ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

		// when
		orderService.placeOrder(userId, request);

		// then
		verify(orderRepository, times(1)).save(orderCaptor.capture());
		Order savedOrder = orderCaptor.getValue();
		assertEquals(userId, orderCaptor.getValue().getUserId());
		assertEquals(request.deliveryComment(), savedOrder.getDeliveryComment());
		assertEquals(request.bucketIds().size(), savedOrder.getOrderDetails().size());

		Integer product1Price = products.get(0).getPrice();
		Integer product2Price = products.get(1).getPrice();
		Integer product1Quantity = bucketServiceResponse.get(0).quantity();
		Integer product2Quantity = bucketServiceResponse.get(1).quantity();
		Integer product1TotalPrice = product1Price * product1Quantity;
		Integer product2TotalPrice = product2Price * product2Quantity;
		List<OrderDetail> savedOrderDetails = savedOrder.getOrderDetails();
		assertEquals(product1TotalPrice, savedOrderDetails.get(0).getTotalPrice());
		assertEquals(product2TotalPrice, savedOrderDetails.get(1).getTotalPrice());

		Integer expectedTotalPaymentAmount = product1TotalPrice + product2TotalPrice;
		assertEquals(expectedTotalPaymentAmount, savedOrder.getTotalPaymentAmount());
		assertEquals(OrderStatus.OPEN, savedOrderDetails.get(0).getStatus());
		assertEquals(OrderStatus.OPEN, savedOrderDetails.get(1).getStatus());
	}

	@Test
	void 재고부족_주문생성_실패() {
		// given
		List<Integer> productIds = List.of(101, 102);
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
		given(stockRepository.findByProductIdIn(anyList()))
				.willReturn(stocks);

		// when
		CustomException exception = assertThrows(CustomException.class,
				() -> orderService.validateStock(productIds, productIdToQuantityMap));

		// then
		assertEquals(INSUFFICIENT_STOCK, exception.getErrorCode());
	}

	@Test
	void 판매중단_상품_주문생성_실패() {
		// given
		final List<Product> products = List.of(
				new Product(
						101,
						"상품 이름1",
						1000,
						"seller1",
						DISCONTINUED
				),
				new Product(
						102,
						"상품 이름2",
						2000,
						"seller2",
						AVAILABLE
				)
		);

		// when
		CustomException exception = assertThrows(CustomException.class,
				() -> orderService.validateProduct(products));

		// then
		assertEquals(NOT_AVAILABLE_PRODUCT, exception.getErrorCode());
	}
}
