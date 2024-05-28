package org.ecommerce.orderapi.bucket.event.listener;

import org.ecommerce.orderapi.bucket.service.BucketDomainService;
import org.ecommerce.orderapi.order.event.OrderCreatedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {

	private final BucketDomainService bucketDomainService;

	@Async
	@TransactionalEventListener
	public void receive(final OrderCreatedEvent event) {
		bucketDomainService.deletedBuckets(event.getBucketIds());
	}
}
