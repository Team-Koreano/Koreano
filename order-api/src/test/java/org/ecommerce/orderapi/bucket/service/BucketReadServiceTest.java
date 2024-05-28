package org.ecommerce.orderapi.bucket.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;

import org.ecommerce.orderapi.bucket.dto.BucketDto;
import org.ecommerce.orderapi.bucket.entity.Bucket;
import org.ecommerce.orderapi.bucket.repository.BucketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BucketReadServiceTest {

	@InjectMocks
	private BucketReadService bucketReadService;

	@Mock
	private BucketRepository bucketRepository;

	@Test
	void 장바구니_조회() {
		// given
		List<Bucket> buckets = List.of(
				new Bucket(1L,
						1,
						"seller1",
						101,
						3,
						LocalDate.of(2024, 5, 2)
				),
				new Bucket(2L,
						1,
						"seller2",
						102,
						2,
						LocalDate.of(2024, 5, 2)
				)
		);
		given(bucketRepository.findAllByUserId(anyInt()))
				.willReturn(buckets);

		// when
		final List<BucketDto> bucketDtos = bucketReadService.getAllBuckets(1);

		// then
		assertEquals(buckets.size(), bucketDtos.size());
		assertEquals(buckets.get(0).getId(), bucketDtos.get(0).id());
		assertEquals(buckets.get(0).getSeller(), bucketDtos.get(0).seller());
		assertEquals(buckets.get(0).getProductId(), bucketDtos.get(0).productId());
		assertEquals(buckets.get(0).getQuantity(), bucketDtos.get(0).quantity());
		assertEquals(buckets.get(1).getId(), bucketDtos.get(1).id());
		assertEquals(buckets.get(1).getSeller(), bucketDtos.get(1).seller());
		assertEquals(buckets.get(1).getProductId(), bucketDtos.get(1).productId());
		assertEquals(buckets.get(1).getQuantity(), bucketDtos.get(1).quantity());
	}
}
