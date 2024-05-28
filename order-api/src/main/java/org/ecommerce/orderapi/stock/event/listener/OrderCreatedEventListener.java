package org.ecommerce.orderapi.stock.event.listener;

import org.ecommerce.orderapi.order.event.OrderCreatedEvent;
import org.ecommerce.orderapi.stock.service.StockDomainService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {

	private final StockDomainService stockDomainService;

	@Async
	@TransactionalEventListener
	public void receive(final OrderCreatedEvent event) {
		stockDomainService.decreaseStocks(event.getDomainId());
	}
}
