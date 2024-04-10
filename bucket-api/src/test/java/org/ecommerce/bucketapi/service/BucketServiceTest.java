package org.ecommerce.bucketapi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.ecommerce.bucketapi.dto.BucketDTO;
import org.ecommerce.bucketapi.entity.Bucket;
import org.ecommerce.bucketapi.repository.BucketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BucketServiceTest {

	@InjectMocks
	private BucketService bucketService;

	@Mock
	private BucketRepository bucketRepository;

	private static final LocalDate CREATE_DATE = LocalDate.now();

	private static final List<Bucket> BUCKETS = List.of(
		new Bucket(1L, 1, 101, 3, CREATE_DATE),
		new Bucket(2L, 1, 102, 2, CREATE_DATE),
		new Bucket(3L, 1, 103, 1, CREATE_DATE)
	);

	private List<BucketDTO.Response> createTestBucketsResponse() {
		List<BucketDTO.Response> bucketResponse = new ArrayList<>();
		bucketResponse.add(new BucketDTO.Response(1L, 1, 101, 3, CREATE_DATE));
		bucketResponse.add(new BucketDTO.Response(2L, 1, 102, 2, CREATE_DATE));
		bucketResponse.add(new BucketDTO.Response(3L, 1, 103, 1, CREATE_DATE));

		return bucketResponse;
	}

	@Test
	void 장바구니_조회() {
		// given
		given(bucketRepository.findAllByUserId(anyInt()))
			.willReturn(BUCKETS);

		// when
		final List<BucketDTO.Response> actual = bucketService.getAllBuckets(1);

		// then
		assertThat(actual).usingRecursiveComparison()
			.isEqualTo(createTestBucketsResponse());
	}
}
