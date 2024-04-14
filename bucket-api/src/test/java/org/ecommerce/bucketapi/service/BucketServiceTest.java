package org.ecommerce.bucketapi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ecommerce.bucketapi.dto.BucketDto;
import org.ecommerce.bucketapi.entity.Bucket;
import org.ecommerce.bucketapi.exception.BucketErrorCode;
import org.ecommerce.bucketapi.repository.BucketRepository;
import org.ecommerce.common.error.CustomException;
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

	private static final Bucket BUCKET = new Bucket(1L, 1, "seller", 101, 3, CREATE_DATE);

	private static final List<Bucket> BUCKETS = List.of(
			new Bucket(1L, 1, "seller1", 101, 3, CREATE_DATE),
			new Bucket(2L, 1, "seller2", 102, 2, CREATE_DATE),
			new Bucket(3L, 1, "seller3", 103, 1, CREATE_DATE)
	);

	private List<BucketDto.Response> getDefaultBucketsResponse() {
		List<BucketDto.Response> bucketResponse = new ArrayList<>();
		bucketResponse.add(new BucketDto.Response(1L, 1, "seller1", 101, 3, CREATE_DATE));
		bucketResponse.add(new BucketDto.Response(2L, 1, "seller2", 102, 2, CREATE_DATE));
		bucketResponse.add(new BucketDto.Response(3L, 1, "seller3", 103, 1, CREATE_DATE));

		return bucketResponse;
	}

	private BucketDto.Response getDefaultBucketResponse() {
		return new BucketDto.Response(1L, 1, "seller", 101, 3, CREATE_DATE);
	}

	@Test
	void 장바구니_조회() {
		// given
		given(bucketRepository.findAllByUserId(anyInt()))
				.willReturn(BUCKETS);

		// when
		final List<BucketDto.Response> actual = bucketService.getAllBuckets(1);

		// then
		assertThat(actual).usingRecursiveComparison()
				.isEqualTo(getDefaultBucketsResponse());
	}

	@Test
	void 장바구니에_담기() {
		// given
		final BucketDto.Request.Add bucketAddRequest = new BucketDto.Request.Add(
				"seller",
				101,
				3
		);
		given(bucketRepository.save(any(Bucket.class)))
				.willReturn(BUCKET);

		// when
		final BucketDto.Response actual = bucketService.addBucket(1, bucketAddRequest);

		// then
		assertThat(actual).isEqualTo(getDefaultBucketResponse());
	}

	@Test
	void 장바구니_수정() {
		// given
		final Integer newQuantity = 777;
		final Bucket bucket = new Bucket(1L, 1, "seller", 101, 3, CREATE_DATE);
		final BucketDto.Request.Update bucketUpdateRequest =
				new BucketDto.Request.Update(newQuantity);

		given(bucketRepository.findById(anyLong()))
				.willReturn(Optional.of(bucket));

		// when
		final BucketDto.Response actual =
				bucketService.updateBucket(1L, bucketUpdateRequest);

		// then
		assertThat(actual.quantity()).isEqualTo(newQuantity);
	}

	@Test
	void 장바구니가_존재하지_않을_때() {
		// given
		final BucketDto.Request.Update bucketUpdateRequest = new BucketDto.Request.Update(
				777);
		given(bucketRepository.findById(anyLong()))
				.willReturn(Optional.empty());

		// when
		// then
		assertThatThrownBy(() -> bucketService.updateBucket(1L, bucketUpdateRequest))
				.isInstanceOf(CustomException.class)
				.extracting("errorCode")
				.isEqualTo(BucketErrorCode.NOT_FOUND_BUCKET_ID)
				.extracting("message")
				.isEqualTo("존재하지 않는 장바구니 번호 입니다.");
	}

	@Test
	void 회원_번호에_대한_장바구니_번호가_유효하지_않을_때() {
		// given
		given(bucketRepository.existsByUserIdAndId(anyInt(), anyLong()))
				.willReturn(false);

		// when
		// then
		assertThatThrownBy(() -> bucketService.validateBucketByUser(1, 1L))
				.isInstanceOf(CustomException.class)
				.extracting("errorCode")
				.isEqualTo(BucketErrorCode.INVALID_BUCKET_WITH_USER)
				.extracting("message")
				.isEqualTo("요청한 유저와 ID에 해당하는 장바구니가 존재하지 않습니다.");
	}
}
