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
import org.springframework.data.domain.Page;

@ExtendWith(MockitoExtension.class)
public class BucketReadServiceTest {

	@InjectMocks
	private BucketReadService bucketReadService;

	@Mock
	private BucketRepository bucketRepository;

	@Test
	void 장바구니_조회() {
		// given
		Integer userId = 1;
		Integer pageNumber = 1;
		Integer pageSize = 2;
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
		given(bucketRepository.findAllByUserId(anyInt(), anyInt(), anyInt()))
				.willReturn(buckets);

		// when
		final Page<BucketDto> bucketDtos =
				bucketReadService.getAllBuckets(userId, pageNumber, pageSize);

		// then
		List<BucketDto> content = bucketDtos.getContent();
		assertEquals(buckets.size(), content.size());
		assertEquals(buckets.get(0).getId(), content.get(0).id());
		assertEquals(buckets.get(0).getSeller(), content.get(0).seller());
		assertEquals(buckets.get(0).getProductId(), content.get(0).productId());
		assertEquals(buckets.get(0).getQuantity(), content.get(0).quantity());
		assertEquals(buckets.get(1).getId(), content.get(1).id());
		assertEquals(buckets.get(1).getSeller(), content.get(1).seller());
		assertEquals(buckets.get(1).getProductId(), content.get(1).productId());
		assertEquals(buckets.get(1).getQuantity(), content.get(1).quantity());
	}
}
