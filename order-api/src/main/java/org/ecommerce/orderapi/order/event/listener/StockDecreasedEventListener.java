package org.ecommerce.orderapi.order.event.listener;

import org.ecommerce.orderapi.order.service.OrderDomainService;
import org.ecommerce.orderapi.stock.event.StockDecreasedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockDecreasedEventListener {

	private final OrderDomainService orderDomainService;

	@Async
	@TransactionalEventListener
	public void receive(final StockDecreasedEvent event) {
		orderDomainService.completeOrder(event.getOrderId(), event.getOrderItemIds());
	}
}
