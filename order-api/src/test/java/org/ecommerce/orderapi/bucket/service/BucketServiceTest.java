package org.ecommerce.orderapi.bucket.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.bucket.dto.BucketDto;
import org.ecommerce.orderapi.bucket.dto.request.AddBucketRequest;
import org.ecommerce.orderapi.bucket.dto.request.ModifyBucketRequest;
import org.ecommerce.orderapi.bucket.entity.Bucket;
import org.ecommerce.orderapi.bucket.exception.BucketErrorCode;
import org.ecommerce.orderapi.bucket.repository.BucketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BucketServiceTest {

	@InjectMocks
	private BucketService bucketService;

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
		final List<BucketDto> bucketDtos = bucketService.getAllBuckets(1);

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

	@Test
	void 장바구니에_담기() {
		// given
		AddBucketRequest bucketAddRequest =
				new AddBucketRequest(
						"inputSellerName",
						103,
						1
				);
		Bucket savedBucket = new Bucket(1L,
				1,
				"seller1",
				101,
				3,
				LocalDate.of(2024, 5, 2)
		);

		given(bucketRepository.save(any(Bucket.class)))
				.willReturn(savedBucket);
		final ArgumentCaptor<Bucket> captor = ArgumentCaptor.forClass(Bucket.class);

		// when
		final BucketDto bucketDto = bucketService.addBucket(
				anyInt(),
				bucketAddRequest
		);

		// then
		verify(bucketRepository, times(1)).save(captor.capture());
		assertEquals(bucketAddRequest.seller(), captor.getValue().getSeller());
		assertEquals(bucketAddRequest.productId(), captor.getValue().getProductId());
		assertEquals(bucketAddRequest.quantity(), captor.getValue().getQuantity());
		assertEquals(savedBucket.getSeller(), bucketDto.seller());
		assertEquals(savedBucket.getProductId(), bucketDto.productId());
		assertEquals(savedBucket.getQuantity(), bucketDto.quantity());
	}

	@Test
	void 장바구니_수정() {
		// given
		final Integer newQuantity = 777;
		final Bucket savedBucket = spy(
				new Bucket(1L,
						1,
						"seller1",
						101,
						3,
						LocalDate.of(2024, 5, 2)
				)
		);
		final ModifyBucketRequest request = new ModifyBucketRequest(newQuantity);
		given(bucketRepository.findByIdAndUserId(anyLong(), anyInt()))
				.willReturn(Optional.of(savedBucket));

		// when
		final BucketDto bucketDto = bucketService.modifyBucket(
				1,
				1L,
				request
		);

		// then
		verify(savedBucket, Mockito.times(1)).modifyQuantity(request.quantity());
		assertEquals(newQuantity, bucketDto.quantity());
	}

	@Test
	void 존재하지_않는_장바구니를_수정할_때() {
		// given
		final ModifyBucketRequest bucketModifyRequest = new ModifyBucketRequest(777);
		given(bucketRepository.findByIdAndUserId(anyLong(), anyInt()))
				.willReturn(Optional.empty());

		// when
		CustomException exception = assertThrows(CustomException.class,
				() -> bucketService.modifyBucket(1, 1L, bucketModifyRequest));

		// then
		assertEquals(BucketErrorCode.NOT_FOUND_BUCKET_ID, exception.getErrorCode());
	}

	@Test
	void 회원이_선택한_장바구니_조회() {
		// given
		final Integer userId = 1;
		final List<Long> bucketIds = List.of(1L, 2L);
		final List<Bucket> buckets = List.of(
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
		given(bucketRepository.findAllByIdInAndUserId(bucketIds, userId))
				.willReturn(buckets);

		// when
		final List<BucketDto> bucketDtos = bucketService.getBuckets(userId, bucketIds);

		// then
		assertEquals(bucketIds.size(), bucketDtos.size());
		assertEquals(buckets.get(0).getId(), bucketDtos.get(0).id());
		assertEquals(buckets.get(0).getSeller(), bucketDtos.get(0).seller());
		assertEquals(buckets.get(0).getProductId(), bucketDtos.get(0).productId());
		assertEquals(buckets.get(0).getQuantity(), bucketDtos.get(0).quantity());
		assertEquals(buckets.get(1).getId(), bucketDtos.get(1).id());
		assertEquals(buckets.get(1).getSeller(), bucketDtos.get(1).seller());
		assertEquals(buckets.get(1).getProductId(), bucketDtos.get(1).productId());
		assertEquals(buckets.get(1).getQuantity(), bucketDtos.get(1).quantity());
	}

	@Test
	void 회원이_선택한_장바구니_누락_조회() {
		// given
		final Integer userId = 1;
		final List<Long> bucketIds = List.of(1L, 2L, 3L);
		final List<Bucket> buckets = List.of(
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
		given(bucketRepository.findAllByIdInAndUserId(anyList(), anyInt()))
				.willReturn(buckets);
		// when
		CustomException exception = assertThrows(CustomException.class,
				() -> bucketService.getBuckets(userId, bucketIds));

		// then
		assertEquals(BucketErrorCode.NOT_FOUND_BUCKET_ID, exception.getErrorCode());
	}
}
