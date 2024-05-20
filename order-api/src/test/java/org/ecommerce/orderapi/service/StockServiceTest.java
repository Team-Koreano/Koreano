// package org.ecommerce.orderapi.service;
//
// import static org.ecommerce.orderapi.entity.enumerated.OrderStatus.*;
// import static org.ecommerce.orderapi.entity.enumerated.OrderStatusReason.*;
// import static org.ecommerce.orderapi.entity.enumerated.StockOperationType.*;
// import static org.ecommerce.orderapi.exception.StockErrorCode.*;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.BDDMockito.*;
//
// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// import org.ecommerce.common.error.CustomException;
// import org.ecommerce.orderapi.dto.StockDto;
// import org.ecommerce.orderapi.entity.OrderItem;
// import org.ecommerce.orderapi.entity.OrderStatusHistory;
// import org.ecommerce.orderapi.entity.Stock;
// import org.ecommerce.orderapi.entity.StockHistory;
// import org.ecommerce.orderapi.repository.OrderItemRepository;
// import org.ecommerce.orderapi.repository.StockHistoryRepository;
// import org.ecommerce.orderapi.repository.StockRepository;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// @ExtendWith(MockitoExtension.class)
// public class StockServiceTest {
//
// 	@InjectMocks
// 	private StockService stockService;
//
// 	@Mock
// 	private OrderItemRepository orderItemRepository;
//
// 	@Mock
// 	private StockRepository stockRepository;
//
// 	@Mock
// 	private StockHistoryRepository stockHistoryRepository;
//
// 	@Test
// 	void 재고_감소() {
// 		// given
// 		List<OrderItem> orderItems = List.of(
// 				spy(new OrderItem(
// 						1L,
// 						null,
// 						101,
// 						"productName1",
// 						10000,
// 						1,
// 						10000,
// 						0,
// 						10000,
// 						1,
// 						"seller1",
// 						OPEN,
// 						null,
// 						LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1),
// 						List.of(
// 								new OrderStatusHistory(
// 										1L,
// 										null,
// 										OPEN,
// 										LocalDateTime.of(2024, 5, 5, 0, 0)
// 								)
// 						)
// 				)),
// 				spy(new OrderItem(
// 						2L,
// 						null,
// 						102,
// 						"productName2",
// 						20000,
// 						1,
// 						20000,
// 						0,
// 						20000,
// 						2,
// 						"seller2",
// 						OPEN,
// 						null,
// 						LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1),
// 						List.of(
// 								new OrderStatusHistory(
// 										2L,
// 										null,
// 										OPEN,
// 										LocalDateTime.of(2024, 5, 5, 0, 0)
// 								)
// 						)
// 				))
// 		);
// 		final Integer initialStockTotal1 = 10;
// 		final Integer initialStockTotal2 = 20;
// 		final Stock stock1 = spy(new Stock(
// 				1,
// 				101,
// 				initialStockTotal1,
// 				LocalDateTime.of(2024, 5, 5, 0, 0),
// 				List.of(
// 						new StockHistory(
// 								1L,
// 								null,
// 								null,
// 								INCREASE,
// 								LocalDateTime.of(2024, 5, 7, 0, 0)
// 						)
// 				))
// 		);
// 		final Stock stock2 = spy(new Stock(
// 				2,
// 				102,
// 				initialStockTotal2,
// 				LocalDateTime.of(2024, 5, 5, 0, 0),
// 				List.of(
// 						new StockHistory(
// 								2L,
// 								null,
// 								null,
// 								INCREASE,
// 								LocalDateTime.of(2024, 5, 7, 0, 0)
// 						)
// 				))
// 		);
// 		int initialStockHistoriesSize1 = stock1.getStockHistories().size();
// 		int initialStockHistoriesSize2 = stock2.getStockHistories().size();
// 		int initialOrderStatusHistoriesSize1 = orderItems.get(0)
// 				.getOrderStatusHistories()
// 				.size();
// 		int initialOrderStatusHistoriesSize2 = orderItems.get(1)
// 				.getOrderStatusHistories()
// 				.size();
// 		Map<Integer, Stock> productToToStockMap = new HashMap<>();
// 		productToToStockMap.put(101, stock1);
// 		productToToStockMap.put(102, stock2);
// 		given(orderItemRepository.findOrderItemsByOrderId(anyLong()))
// 				.willReturn(orderItems);
// 		given(stockRepository.findStocksByProductIdIn(anyList()))
// 				.willReturn(productToToStockMap);
//
// 		// when
// 		stockService.decreaseStocks(anyLong());
//
// 		// then
// 		OrderItem orderItem1 = orderItems.get(0);
// 		OrderItem orderItem2 = orderItems.get(1);
//
// 		verify(orderItem1).changeStatus(eq(CLOSED), any());
// 		verify(orderItem2).changeStatus(eq(CLOSED), any());
//
// 		verify(stock1).decreaseTotalStock(
// 				eq(orderItem1.getQuantity()), any(OrderItem.class));
// 		verify(stock2).decreaseTotalStock(
// 				eq(orderItem2.getQuantity()), any(OrderItem.class));
//
// 		assertEquals(initialStockTotal1 - orderItem1.getQuantity(), stock1.getTotal());
// 		assertEquals(initialStockTotal2 - orderItem2.getQuantity(), stock2.getTotal());
//
// 		assertEquals(initialStockHistoriesSize1 + 1, stock1.getStockHistories().size());
// 		assertEquals(initialStockHistoriesSize2 + 1, stock2.getStockHistories().size());
//
// 		assertEquals(initialOrderStatusHistoriesSize1 + 1,
// 				orderItem1.getOrderStatusHistories().size());
// 		assertEquals(initialOrderStatusHistoriesSize2 + 1,
// 				orderItem2.getOrderStatusHistories().size());
// 	}
//
// 	@Test
// 	void 재고부족_감소_실패() {
// 		// given
// 		List<OrderItem> orderItems = List.of(
// 				spy(new OrderItem(
// 						1L,
// 						null,
// 						101,
// 						"productName1",
// 						10000,
// 						1,
// 						10000,
// 						0,
// 						10000,
// 						1,
// 						"seller1",
// 						OPEN,
// 						null,
// 						LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1),
// 						List.of(
// 								new OrderStatusHistory(
// 										1L,
// 										null,
// 										OPEN,
// 										LocalDateTime.of(2024, 5, 5, 0, 0)
// 								)
// 						)
// 				)),
// 				spy(new OrderItem(
// 						2L,
// 						null,
// 						102,
// 						"productName2",
// 						20000,
// 						20,
// 						20000,
// 						0,
// 						20000,
// 						2,
// 						"seller2",
// 						OPEN,
// 						null,
// 						LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1),
// 						List.of(
// 								new OrderStatusHistory(
// 										2L,
// 										null,
// 										OPEN,
// 										LocalDateTime.of(2024, 5, 5, 0, 0)
// 								)
// 						)
// 				))
// 		);
// 		final Integer initialStockTotal1 = 10;
// 		final Integer initialStockTotal2 = 10;
// 		final Stock stock1 = spy(new Stock(
// 				1,
// 				101,
// 				initialStockTotal1,
// 				LocalDateTime.of(2024, 5, 5, 0, 0),
// 				List.of(
// 						new StockHistory(
// 								1L,
// 								null,
// 								null,
// 								INCREASE,
// 								LocalDateTime.of(2024, 5, 7, 0, 0)
// 						)
// 				))
// 		);
// 		final Stock stock2 = spy(new Stock(
// 				2,
// 				102,
// 				initialStockTotal2,
// 				LocalDateTime.of(2024, 5, 5, 0, 0),
// 				List.of(
// 						new StockHistory(
// 								2L,
// 								null,
// 								null,
// 								INCREASE,
// 								LocalDateTime.of(2024, 5, 7, 0, 0)
// 						)
// 				))
// 		);
// 		Map<Integer, Stock> productToToStockMap = new HashMap<>();
// 		productToToStockMap.put(101, stock1);
// 		productToToStockMap.put(102, stock2);
//
// 		// when
// 		boolean decreaseStockResult = stockService
// 				.decreaseStock(orderItems, productToToStockMap);
//
// 		// then
// 		assertFalse(decreaseStockResult);
// 		assertEquals(initialStockTotal2, stock2.getTotal());
// 		verify(stock2, never()).decreaseTotalStock(anyInt(), any(OrderItem.class));
// 	}
//
// 	@Test
// 	void 재고_증가() {
// 		// given
// 		final List<OrderStatusHistory> orderStatusHistories = List.of(
// 				new OrderStatusHistory(
// 						1L,
// 						null,
// 						OPEN,
// 						LocalDateTime.of(2024, 5, 9, 0, 0)
// 				)
// 		);
// 		final OrderItem orderItem = new OrderItem(
// 				1L,
// 				null,
// 				101,
// 				"productName1",
// 				10000,
// 				1,
// 				10000,
// 				0,
// 				10000,
// 				1,
// 				"sellerName",
// 				CANCELLED,
// 				REFUND,
// 				LocalDateTime.of(2024, 5, 9, 0, 0),
// 				orderStatusHistories
// 		);
// 		final Stock stock = spy(new Stock(
// 				1,
// 				101,
// 				9,
// 				LocalDateTime.of(2024, 5, 9, 0, 0),
// 				List.of(
// 						new StockHistory(
// 								1L,
// 								null,
// 								orderItem,
// 								DECREASE,
// 								LocalDateTime.of(2024, 5, 9, 0, 0)
// 						)
// 				)
// 		));
// 		final StockHistory stockHistory = new StockHistory(
// 				1L,
// 				stock,
// 				orderItem,
// 				DECREASE,
// 				LocalDateTime.of(2024, 5, 9, 0, 0)
// 		);
// 		given(orderItemRepository.findOrderItemById(anyLong(), isNull()))
// 				.willReturn(orderItem);
// 		given(stockHistoryRepository.findStockHistoryByOrderItemId(anyLong()))
// 				.willReturn(stockHistory);
// 		final int previousStockHistoriesSize = stock.getStockHistories().size();
// 		final int previousStockTotal = stock.getTotal();
// 		// when
// 		StockDto stockDto = stockService.increaseStock(orderItem.getId());
//
// 		// then
// 		verify(stock, Mockito.times(1))
// 				.increaseTotalStock(orderItem);
// 		List<StockHistory> stockHistories = stock.getStockHistories();
// 		assertEquals(previousStockHistoriesSize + 1, stockHistories.size());
// 		assertEquals(previousStockTotal + orderItem.getQuantity(), stockDto.getTotal());
// 		assertEquals(INCREASE, stockHistories.get(1).getOperationType());
// 	}
//
// 	@Test
// 	void 잘못된_주문상태_재고증가_실패() {
// 		// given
// 		final OrderItem orderItem = spy(new OrderItem(
// 				1L,
// 				null,
// 				101,
// 				"productName1",
// 				10000,
// 				1,
// 				10000,
// 				0,
// 				10000,
// 				1,
// 				"sellerName",
// 				OPEN,
// 				null,
// 				LocalDateTime.of(2024, 5, 9, 0, 0),
// 				null
// 		));
// 		// when
// 		CustomException exception = assertThrows(CustomException.class,
// 				() -> stockService.validateOrderItem(orderItem));
//
// 		// then
// 		verify(orderItem, Mockito.times(1)).isRefundedOrder();
// 		assertEquals(MUST_CANCELLED_ORDER_TO_INCREASE_STOCK, exception.getErrorCode());
// 	}
//
// 	@Test
// 	void 잘못된_재고이력_재고증가_실패() {
// 		// given
// 		StockHistory stockHistory = spy(new StockHistory(
// 				1L,
// 				null,
// 				null,
// 				INCREASE,
// 				LocalDateTime.of(2024, 5, 9, 0, 0)
// 		));
// 		// when
// 		CustomException exception = assertThrows(CustomException.class,
// 				() -> stockService.validateStockHistory(stockHistory));
//
// 		// then
// 		verify(stockHistory, Mockito.times(1)).isOperationTypeDecrease();
// 		assertEquals(MUST_DECREASE_STOCK_OPERATION_TYPE_TO_INCREASE_STOCK,
// 				exception.getErrorCode());
// 	}
// }
