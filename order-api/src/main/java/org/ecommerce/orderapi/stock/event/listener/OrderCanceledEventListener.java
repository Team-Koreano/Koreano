package org.ecommerce.orderapi.stock.event.listener;

import org.ecommerce.orderapi.order.event.OrderCanceledEvent;
import org.ecommerce.orderapi.stock.service.StockDomainService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCanceledEventListener {

	private final StockDomainService stockDomainService;

	@Async
	@TransactionalEventListener
	public void receive(final OrderCanceledEvent event) {
		stockDomainService.increaseStock(event.getOrderItemId());
	}
}
