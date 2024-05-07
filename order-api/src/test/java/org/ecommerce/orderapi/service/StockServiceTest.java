package org.ecommerce.orderapi.service;

import static org.ecommerce.orderapi.entity.enumerated.OrderStatus.*;
import static org.ecommerce.orderapi.entity.enumerated.StockOperationType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ecommerce.orderapi.entity.OrderDetail;
import org.ecommerce.orderapi.entity.OrderStatusHistory;
import org.ecommerce.orderapi.entity.Stock;
import org.ecommerce.orderapi.entity.StockHistory;
import org.ecommerce.orderapi.repository.OrderDetailRepository;
import org.ecommerce.orderapi.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

	@InjectMocks
	private StockService stockService;

	@Mock
	private OrderDetailRepository orderDetailRepository;

	@Mock
	private StockRepository stockRepository;

	@Test
	void 재고_감소() {
		// given
		List<OrderDetail> orderDetails = List.of(
				spy(new OrderDetail(
						1L,
						null,
						101,
						"productName1",
						10000,
						1,
						10000,
						0,
						10000,
						1,
						"seller1",
						OPEN,
						null,
						List.of(
								new OrderStatusHistory(
										1L,
										null,
										OPEN,
										LocalDateTime.of(2024, 5, 5, 0, 0)
								)
						)
				)),
				spy(new OrderDetail(
						2L,
						null,
						102,
						"productName2",
						20000,
						1,
						20000,
						0,
						20000,
						2,
						"seller2",
						OPEN,
						null,
						List.of(
								new OrderStatusHistory(
										2L,
										null,
										OPEN,
										LocalDateTime.of(2024, 5, 5, 0, 0)
								)
						)
				))
		);
		final Integer initialStockTotal1 = 10;
		final Integer initialStockTotal2 = 20;
		final Stock stock1 = spy(new Stock(
				1,
				101,
				initialStockTotal1,
				LocalDateTime.of(2024, 5, 5, 0, 0),
				List.of(
						new StockHistory(
								1L,
								null,
								null,
								INCREASE,
								LocalDateTime.of(2024,5,7,0,0)
						)
				))
		);
		final Stock stock2 = spy(new Stock(
				2,
				102,
				initialStockTotal2,
				LocalDateTime.of(2024, 5, 5, 0, 0),
				List.of(
						new StockHistory(
								2L,
								null,
								null,
								INCREASE,
								LocalDateTime.of(2024,5,7,0,0)
						)
				))
		);
		int initialStockHistoriesSize1 = stock1.getStockHistories().size();
		int initialStockHistoriesSize2 = stock2.getStockHistories().size();
		int initialOrderStatusHistoriesSize1 = orderDetails.get(0)
				.getOrderStatusHistories()
				.size();
		int initialOrderStatusHistoriesSize2 = orderDetails.get(1)
				.getOrderStatusHistories()
				.size();
		Map<Integer, Stock> productToToStockMap = new HashMap<>();
		productToToStockMap.put(101, stock1);
		productToToStockMap.put(102, stock2);
		given(orderDetailRepository.findOrderDetailsByOrderId(anyLong()))
				.willReturn(orderDetails);
		given(stockRepository.findStocksByProductIdIn(anyList()))
				.willReturn(productToToStockMap);

		// when
		stockService.decreaseStocks(anyLong());

		// then
		OrderDetail orderDetail1 = orderDetails.get(0);
		OrderDetail orderDetail2 = orderDetails.get(1);

		verify(orderDetail1).changeStatus(eq(CLOSED), any());
		verify(orderDetail2).changeStatus(eq(CLOSED), any());

		verify(stock1).decreaseTotalStock(
				eq(orderDetail1.getQuantity()), any(OrderDetail.class));
		verify(stock2).decreaseTotalStock(
				eq(orderDetail2.getQuantity()), any(OrderDetail.class));

		assertEquals(initialStockTotal1 - orderDetail1.getQuantity(), stock1.getTotal());
		assertEquals(initialStockTotal2 - orderDetail2.getQuantity(), stock2.getTotal());

		assertEquals(initialStockHistoriesSize1 + 1, stock1.getStockHistories().size());
		assertEquals(initialStockHistoriesSize2 + 1, stock2.getStockHistories().size());

		assertEquals(initialOrderStatusHistoriesSize1 + 1,
				orderDetail1.getOrderStatusHistories().size());
		assertEquals(initialOrderStatusHistoriesSize2 + 1,
				orderDetail2.getOrderStatusHistories().size());
	}

	@Test
	void 재고부족_감소_실패() {
		// given
		List<OrderDetail> orderDetails = List.of(
				spy(new OrderDetail(
						1L,
						null,
						101,
						"productName1",
						10000,
						1,
						10000,
						0,
						10000,
						1,
						"seller1",
						OPEN,
						null,
						List.of(
								new OrderStatusHistory(
										1L,
										null,
										OPEN,
										LocalDateTime.of(2024, 5, 5, 0, 0)
								)
						)
				)),
				spy(new OrderDetail(
						2L,
						null,
						102,
						"productName2",
						20000,
						20,
						20000,
						0,
						20000,
						2,
						"seller2",
						OPEN,
						null,
						List.of(
								new OrderStatusHistory(
										2L,
										null,
										OPEN,
										LocalDateTime.of(2024, 5, 5, 0, 0)
								)
						)
				))
		);
		final Integer initialStockTotal1 = 10;
		final Integer initialStockTotal2 = 10;
		final Stock stock1 = spy(new Stock(
				1,
				101,
				initialStockTotal1,
				LocalDateTime.of(2024, 5, 5, 0, 0),
				List.of(
						new StockHistory(
								1L,
								null,
								null,
								INCREASE,
								LocalDateTime.of(2024,5,7,0,0)
						)
				))
		);
		final Stock stock2 = spy(new Stock(
				2,
				102,
				initialStockTotal2,
				LocalDateTime.of(2024, 5, 5, 0, 0),
				List.of(
						new StockHistory(
								2L,
								null,
								null,
								INCREASE,
								LocalDateTime.of(2024,5,7,0,0)
						)
				))
		);
		Map<Integer, Stock> productToToStockMap = new HashMap<>();
		productToToStockMap.put(101, stock1);
		productToToStockMap.put(102, stock2);

		// when
		boolean decreaseStockResult = stockService
				.decreaseStock(orderDetails, productToToStockMap);

		// then
		assertFalse(decreaseStockResult);
		assertEquals(initialStockTotal2, stock2.getTotal());
		verify(stock2, never()).decreaseTotalStock(anyInt(), any(OrderDetail.class));
	}
}
