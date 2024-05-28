package org.ecommerce.orderapi.bucket.event.listener;

import java.util.List;

import org.ecommerce.orderapi.bucket.repository.BucketRepository;
import org.ecommerce.orderapi.order.event.OrderCreatedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {

	private final BucketCleanupService bucketCleanupService;

	@TransactionalEventListener(fallbackExecution = true)
	public void receive(final OrderCreatedEvent event) {
		bucketCleanupService.deleteBuckets(event.getBucketIds());
	}

	@Service
	@RequiredArgsConstructor
	static class BucketCleanupService {

		private final BucketRepository bucketRepository;

		@Async
		@Transactional(propagation = Propagation.REQUIRES_NEW)
		void deleteBuckets(final List<Long> bucketIds) {
			bucketRepository.deleteAll(bucketRepository.findAllByInId(bucketIds));
		}
	}
}
