package org.ecommerce.bucketapi.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.ecommerce.bucketapi.dto.BucketDto;
import org.ecommerce.bucketapi.entity.Bucket;
import org.ecommerce.bucketapi.exception.BucketErrorCode;
import org.ecommerce.bucketapi.repository.BucketRepository;
import org.ecommerce.common.error.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

	@Test
	void 장바구니_조회() {
		// given
		List<Bucket> buckets = List.of(
				new Bucket(1L, 1, "seller1", 101, 3, CREATE_DATE),
				new Bucket(2L, 1, "seller2", 102, 2, CREATE_DATE)
		);
		given(bucketRepository.findAllByUserId(anyInt()))
				.willReturn(buckets);

		// when
		final List<BucketDto> bucketDtos = bucketService.getAllBuckets(1);

		// then
		assertEquals(2, bucketDtos.size());
		assertEquals(1L, bucketDtos.get(0).getId());
		assertEquals("seller1", bucketDtos.get(0).getSeller());
		assertEquals(101, bucketDtos.get(0).getProductId());
		assertEquals(3, bucketDtos.get(0).getQuantity());
		assertEquals(2L, bucketDtos.get(1).getId());
		assertEquals("seller2", bucketDtos.get(1).getSeller());
		assertEquals(102, bucketDtos.get(1).getProductId());
		assertEquals(2, bucketDtos.get(1).getQuantity());
	}

	@Test
	void 장바구니에_담기() {
		// given
		given(bucketRepository.save(any(Bucket.class)))
				.willReturn(
						new Bucket(
								1L,
								1,
								"returnSellerName",
								101,
								3,
								CREATE_DATE
						)
				);
		final ArgumentCaptor<Bucket> captor = ArgumentCaptor.forClass(Bucket.class);

		// when
		final BucketDto bucketDto = bucketService.addBucket(
				1,
				new BucketDto.Request.Add(
						"inputSellerName",
						103,
						1
				)
		);

		// then
		verify(bucketRepository, times(1)).save(captor.capture());
		assertEquals("inputSellerName", captor.getValue().getSeller());
		assertEquals(103, captor.getValue().getProductId());
		assertEquals(1, captor.getValue().getQuantity());
		assertEquals("returnSellerName", bucketDto.getSeller());
		assertEquals(101, bucketDto.getProductId());
		assertEquals(3, bucketDto.getQuantity());
	}

	@Test
	void 장바구니_수정() {
		// given
		final Integer newQuantity = 777;
		given(bucketRepository.findById(anyLong()))
				.willReturn(Optional.of(
								new Bucket(
										1L,
										1,
										"seller",
										101,
										3,
										CREATE_DATE)
						)
				);

		// when
		final BucketDto bucketDto = bucketService.updateBucket(
				1L,
				new BucketDto.Request.Update(newQuantity)
		);

		// then
		assertEquals(newQuantity, bucketDto.getQuantity());
	}

	@Test
	void 장바구니가_존재하지_않을_때() {
		// given
		final BucketDto.Request.Update bucketUpdateRequest =
				new BucketDto.Request.Update(777);
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
