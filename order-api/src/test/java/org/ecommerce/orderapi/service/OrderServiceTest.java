package org.ecommerce.orderapi.service;


import static org.ecommerce.orderapi.entity.enumerated.ProductStatus.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;

import org.ecommerce.orderapi.dto.BucketDto;
import org.ecommerce.orderapi.dto.BucketSummary;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.entity.Product;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.repository.OrderRepository;
import org.ecommerce.orderapi.util.ProductOperation;
import org.ecommerce.orderapi.util.StockOperation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

	@InjectMocks
	private OrderService orderService;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private RedissonClient redissonClient;

	@Test
	void 주문_생성() {
	    // given
		final Integer userId = 1;
		final OrderDto.Request.Place request = new OrderDto.Request.Place(
				List.of(1L, 2L, 3L),
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드립니다."
		);
		final List<BucketDto> bucketDtos = List.of(
				new BucketDto(
						1L,
						1,
						"seller1",
						101,
						1,
						LocalDate.of(2024,5,1)
				),
				new BucketDto(
						2L,
						1,
						"seller2",
						102,
						2,
						LocalDate.of(2024,5,1)
				)
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
						101,
						10
				),
				new Stock(
						102,
						20
				)
		);
		given(orderService.getBuckets(anyInt(), anyList()))
				.willReturn(BucketSummary.ofCreate(bucketDtos));
		given(ProductOperation.getProducts(redissonClient, anyList()))
				.willReturn(products);
		given(StockOperation.getStocks(redissonClient, anyList()))
				.willReturn(stocks);

		ArgumentCaptor<Order> oderCaptor = ArgumentCaptor.forClass(Order.class);

	    // when
		OrderDto orderDto = orderService.placeOrder(userId, request);

		// then
		verify(stocks.get(0).hasStock(anyInt()), times(1));
		verify(stocks.get(1).hasStock(anyInt()), times(1));
		verify(orderRepository, times(1)).save(oderCaptor.capture());
	}

}
