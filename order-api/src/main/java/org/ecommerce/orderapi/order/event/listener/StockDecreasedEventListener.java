package org.ecommerce.orderapi.order.event.listener;

import java.util.Set;

import org.ecommerce.orderapi.order.repository.OrderRepository;
import org.ecommerce.orderapi.stock.event.StockDecreasedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockDecreasedEventListener {

	private final OrderCompletionService orderCompletionService;

	@TransactionalEventListener
	public void receive(final StockDecreasedEvent event) {
		orderCompletionService.completeOrder(event.getOrderId(), event.getOrderItemIds());
	}

	@Service
	@RequiredArgsConstructor
	static class OrderCompletionService {

		private final OrderRepository orderRepository;

		@Async
		@Transactional(propagation = Propagation.REQUIRES_NEW)
		void completeOrder(final Long orderId, final Set<Long> orderItemIds) {
			orderRepository.findOrderById(orderId).complete(orderItemIds);
		}
	}
}
