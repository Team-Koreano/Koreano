package org.ecommerce.orderapi.stock.service;

import static org.ecommerce.orderapi.order.entity.enumerated.OrderStatus.*;
import static org.ecommerce.orderapi.order.entity.enumerated.OrderStatusReason.*;
import static org.ecommerce.orderapi.stock.entity.enumerated.StockOperationResult.*;
import static org.ecommerce.orderapi.stock.entity.enumerated.StockOperationType.*;
import static org.ecommerce.orderapi.stock.exception.StockErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.order.entity.Order;
import org.ecommerce.orderapi.order.entity.OrderItem;
import org.ecommerce.orderapi.order.entity.OrderStatusHistory;
import org.ecommerce.orderapi.order.repository.OrderItemRepository;
import org.ecommerce.orderapi.order.repository.OrderRepository;
import org.ecommerce.orderapi.stock.entity.Stock;
import org.ecommerce.orderapi.stock.entity.StockHistory;
import org.ecommerce.orderapi.stock.event.publisher.StockDecreasedEventKafkaPublisher;
import org.ecommerce.orderapi.stock.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
public class StockDomainServiceTest {

	@InjectMocks
	private StockDomainService stockDomainService;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderItemRepository orderItemRepository;

	@Mock
	private StockRepository stockRepository;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@Mock
	private StockDecreasedEventKafkaPublisher stockDecreasedEventKafkaPublisher;

	@Test
	void 재고_감소() {
		// given
		final Integer initialStockTotal1 = 10;
		final Integer initialStockTotal2 = 20;
		Order order = spy(new Order(
				1L,
				1,
				"userName",
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드립니다.",
				0,
				APPROVE,
				LocalDateTime.of(2024, 5, 22, 0, 0),
				LocalDateTime.of(2024, 5, 22, 0, 0),
				LocalDateTime.of(2024, 5, 22, 0, 0),
				List.of(
						new OrderItem(
								1L,
								null,
								101,
								"에디오피아 아가체프",
								1000,
								10,
								10000,
								0,
								10000,
								1,
								"seller1",
								APPROVE,
								null,
								LocalDateTime.of(2024, 5, 22, 0, 0),
								LocalDateTime.of(2024, 5, 22, 0, 0),
								new ArrayList<>()
						),
						new OrderItem(
								2L,
								null,
								102,
								"과테말라 안티구아",
								2000,
								20,
								40000,
								0,
								40000,
								2,
								"seller2",
								APPROVE,
								null,
								LocalDateTime.of(2024, 5, 22, 0, 0),
								LocalDateTime.of(2024, 5, 22, 0, 0),
								new ArrayList<>()
						)
				)
		));

		final List<StockHistory> stockHistories1 = new ArrayList<>();
		stockHistories1.add(new StockHistory(
				1L,
				null,
				null,
				INCREASE,
				SUCCESS,
				LocalDateTime.of(2024, 5, 7, 0, 0)
		));
		final Stock stock1 = spy(
				new Stock(
						1,
						101,
						initialStockTotal1,
						LocalDateTime.of(2024, 5, 5, 0, 0),
						stockHistories1
				)
		);
		final List<StockHistory> stockHistories2 = new ArrayList<>();
		stockHistories2.add(
				new StockHistory(
						2L,
						null,
						null,
						INCREASE,
						SUCCESS,
						LocalDateTime.of(2024, 5, 7, 0, 0)
				)
		);
		final Stock stock2 = spy(
				new Stock(
						2,
						102,
						initialStockTotal2,
						LocalDateTime.of(2024, 5, 5, 0, 0),
						stockHistories2
				)
		);
		final int initialStockHistoriesSize1 = stockHistories1.size();
		final int initialStockHistoriesSize2 = stockHistories2.size();
		Map<Integer, Stock> productToToStockMap = new HashMap<>();
		productToToStockMap.put(stock1.getProductId(), stock1);
		productToToStockMap.put(stock2.getProductId(), stock2);
		given(orderRepository.findOrderById(anyLong()))
				.willReturn(order);
		given(stockRepository.findStocksByProductIdIn(anyList()))
				.willReturn(productToToStockMap);

		// when
		stockDomainService.decreaseStocks(anyLong());

		// then
		OrderItem orderItem1 = order.getOrderItems().get(0);
		OrderItem orderItem2 = order.getOrderItems().get(1);

		verify(stock1, times(1))
				.decreaseTotal(orderItem1.getId(), orderItem1.getQuantity());
		verify(stock2, times(1))
				.decreaseTotal(orderItem2.getId(), orderItem2.getQuantity());
		assertEquals(initialStockTotal1 - orderItem1.getQuantity(), stock1.getTotal());
		assertEquals(initialStockTotal2 - orderItem2.getQuantity(), stock2.getTotal());
		assertEquals(initialStockHistoriesSize1 + 1, stock1.getStockHistories().size());
		assertEquals(initialStockHistoriesSize2 + 1, stock2.getStockHistories().size());
		assertEquals(SUCCESS, stock1.getStockHistories().get(1).getOperationResult());
		assertEquals(SUCCESS, stock2.getStockHistories().get(1).getOperationResult());
		assertEquals(DECREASE, stock1.getStockHistories().get(1).getOperationType());
		assertEquals(DECREASE, stock2.getStockHistories().get(1).getOperationType());
	}

	@Test
	void 재고부족_감소_실패() {
		// given
		final Integer initialStockTotal1 = 0;
		final Integer initialStockTotal2 = 0;
		Order order = spy(new Order(
				1L,
				1,
				"userName",
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드립니다.",
				0,
				APPROVE,
				LocalDateTime.of(2024, 5, 22, 0, 0),
				LocalDateTime.of(2024, 5, 22, 0, 0),
				LocalDateTime.of(2024, 5, 22, 0, 0),
				List.of(
						new OrderItem(
								1L,
								null,
								101,
								"에디오피아 아가체프",
								1000,
								10,
								10000,
								0,
								10000,
								1,
								"seller1",
								APPROVE,
								null,
								LocalDateTime.of(2024, 5, 22, 0, 0),
								LocalDateTime.of(2024, 5, 22, 0, 0),
								new ArrayList<>()
						),
						new OrderItem(
								2L,
								null,
								102,
								"과테말라 안티구아",
								2000,
								20,
								40000,
								0,
								40000,
								2,
								"seller2",
								APPROVE,
								null,
								LocalDateTime.of(2024, 5, 22, 0, 0),
								LocalDateTime.of(2024, 5, 22, 0, 0),
								new ArrayList<>()
						)
				)
		));

		final List<StockHistory> stockHistories1 = new ArrayList<>();
		stockHistories1.add(new StockHistory(
				1L,
				null,
				null,
				INCREASE,
				SUCCESS,
				LocalDateTime.of(2024, 5, 7, 0, 0)
		));
		final Stock stock1 = spy(
				new Stock(
						1,
						101,
						initialStockTotal1,
						LocalDateTime.of(2024, 5, 5, 0, 0),
						stockHistories1
				)
		);
		final List<StockHistory> stockHistories2 = new ArrayList<>();
		stockHistories2.add(
				new StockHistory(
						2L,
						null,
						null,
						INCREASE,
						SUCCESS,
						LocalDateTime.of(2024, 5, 7, 0, 0)
				)
		);
		final Stock stock2 = spy(
				new Stock(
						2,
						102,
						initialStockTotal2,
						LocalDateTime.of(2024, 5, 5, 0, 0),
						stockHistories2
				)
		);
		Map<Integer, Stock> productToToStockMap = new HashMap<>();
		productToToStockMap.put(stock1.getProductId(), stock1);
		productToToStockMap.put(stock2.getProductId(), stock2);
		given(orderRepository.findOrderById(anyLong()))
				.willReturn(order);
		given(stockRepository.findStocksByProductIdIn(anyList()))
				.willReturn(productToToStockMap);

		// when
		stockDomainService.decreaseStocks(anyLong());

		// then
		OrderItem orderItem1 = order.getOrderItems().get(0);
		OrderItem orderItem2 = order.getOrderItems().get(1);
		verify(stock1, times(1))
				.decreaseTotal(orderItem1.getId(), orderItem1.getQuantity());
		verify(stock2, times(1))
				.decreaseTotal(orderItem2.getId(), orderItem2.getQuantity());
		assertEquals(DECREASE, stockHistories1.get(1).getOperationType());
		assertEquals(DECREASE, stockHistories2.get(1).getOperationType());
		assertEquals(SOLD_OUT, stockHistories1.get(1).getOperationResult());
		assertEquals(SOLD_OUT, stockHistories2.get(1).getOperationResult());
	}

	@Test
	void 재고_증가() {
		// given
		final List<OrderStatusHistory> orderStatusHistories = List.of(
				new OrderStatusHistory(
						1L,
						null,
						OPEN,
						LocalDateTime.of(2024, 5, 9, 0, 0)
				)
		);
		final OrderItem orderItem = new OrderItem(
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
				"sellerName",
				CANCELLED,
				REFUND,
				LocalDateTime.of(2024, 5, 9, 0, 0),
				LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1),
				orderStatusHistories
		);
		List<StockHistory> stockHistories = new ArrayList<>();
		stockHistories.add(
				new StockHistory(
						1L,
						null,
						orderItem.getId(),
						DECREASE,
						SUCCESS,
						LocalDateTime.of(2024, 5, 9, 0, 0)
				)
		);
		final Stock stock = spy(new Stock(
				1,
				101,
				9,
				LocalDateTime.of(2024, 5, 9, 0, 0),
				stockHistories
		));

		given(orderItemRepository.findOrderItemById(anyLong()))
				.willReturn(orderItem);
		given(stockRepository.findStockByOrderItemId(anyLong()))
				.willReturn(stock);
		final int previousStockHistoriesSize = stock.getStockHistories().size();
		final int previousStockTotal = stock.getTotal();
		// when
		stockDomainService.increaseStock(orderItem.getId());

		// then
		verify(stock, times(1))
				.increaseTotal(orderItem.getId(), orderItem.getQuantity());
		assertEquals(previousStockTotal + orderItem.getQuantity(), stock.getTotal());
		assertEquals(previousStockHistoriesSize + 1, stockHistories.size());
		assertEquals(INCREASE, stockHistories.get(1).getOperationType());
		assertEquals(SUCCESS, stockHistories.get(1).getOperationResult());
	}

	@Test
	void 잘못된_주문상태_재고증가_실패() {
		// given
		final OrderItem orderItem = spy(
				new OrderItem(
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
						"sellerName",
						CLOSED,
						REFUND,
						LocalDateTime.of(2024, 5, 9, 0, 0),
						LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1),
						new ArrayList<>()
				)
		);
		// when
		CustomException exception = assertThrows(CustomException.class,
				() -> stockDomainService.validateOrderItem(orderItem));

		// then
		verify(orderItem, times(1)).isRefundedOrderStatus();
		assertEquals(MUST_CANCELLED_ORDER_TO_INCREASE_STOCK, exception.getErrorCode());
	}

	@Test
	void 잘못된_재고이력_재고증가_실패() {
		// given
		final List<OrderStatusHistory> orderStatusHistories = List.of(
				new OrderStatusHistory(
						1L,
						null,
						OPEN,
						LocalDateTime.of(2024, 5, 9, 0, 0)
				)
		);
		final OrderItem orderItem = new OrderItem(
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
				"sellerName",
				CANCELLED,
				REFUND,
				LocalDateTime.of(2024, 5, 9, 0, 0),
				LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1),
				orderStatusHistories
		);
		List<StockHistory> stockHistories = new ArrayList<>();
		stockHistories.add(
				new StockHistory(
						1L,
						null,
						orderItem.getId(),
						INCREASE,
						SUCCESS,
						LocalDateTime.of(2024, 5, 9, 0, 0)
				)
		);
		final Stock stock = spy(new Stock(
				1,
				101,
				9,
				LocalDateTime.of(2024, 5, 9, 0, 0),
				stockHistories
		));

		given(orderItemRepository.findOrderItemById(anyLong()))
				.willReturn(orderItem);
		given(stockRepository.findStockByOrderItemId(anyLong()))
				.willReturn(stock);
		// when
		CustomException exception = assertThrows(CustomException.class,
				() -> stockDomainService.increaseStock(anyLong()));

		// then
		verify(stock, times(1))
				.increaseTotal(orderItem.getId(), orderItem.getQuantity());
		assertEquals(MUST_DECREASE_STOCK_OPERATION_TYPE_TO_INCREASE_STOCK,
				exception.getErrorCode());
	}
}
