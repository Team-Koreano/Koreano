package org.ecommerce.orderapi.bucket.service;

import static org.ecommerce.orderapi.order.entity.enumerated.ProductStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.bucket.dto.BucketDto;
import org.ecommerce.orderapi.bucket.dto.request.AddBucketRequest;
import org.ecommerce.orderapi.bucket.dto.request.ModifyBucketRequest;
import org.ecommerce.orderapi.bucket.entity.Bucket;
import org.ecommerce.orderapi.bucket.exception.BucketErrorCode;
import org.ecommerce.orderapi.bucket.repository.BucketRepository;
import org.ecommerce.orderapi.global.client.ProductServiceClient;
import org.ecommerce.orderapi.order.dto.response.ProductResponse;
import org.ecommerce.orderapi.stock.entity.Stock;
import org.ecommerce.orderapi.stock.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BucketDomainServiceTest {

	@InjectMocks
	private BucketDomainService bucketDomainService;

	@Mock
	private BucketRepository bucketRepository;

	@Mock
	private StockRepository stockRepository;

	@Mock
	private ProductServiceClient productServiceClient;

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

		ProductResponse productResponse = new ProductResponse(
				101,
				"상품 이름",
				10000,
				1,
				"판매자 이름",
				AVAILABLE
		);

		Stock stock = new Stock(
				1,
				101,
				10,
				LocalDateTime.of(2024, 5, 28, 0, 0),
				List.of()
		);
		given(productServiceClient.getProduct(anyInt()))
				.willReturn(productResponse);
		given(stockRepository.findStockByProductId(anyInt()))
				.willReturn(stock);
		given(bucketRepository.save(any(Bucket.class)))
				.willReturn(savedBucket);
		final ArgumentCaptor<Bucket> captor = ArgumentCaptor.forClass(Bucket.class);

		// when
		final BucketDto bucketDto = bucketDomainService.addBucket(
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
				.willReturn(savedBucket);

		// when
		final BucketDto bucketDto = bucketDomainService.modifyBucket(
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
				.willReturn(null);

		// when
		CustomException exception = assertThrows(CustomException.class,
				() -> bucketDomainService.modifyBucket(1, 1L, bucketModifyRequest));

		// then
		assertEquals(BucketErrorCode.NOT_FOUND_BUCKET_ID, exception.getErrorCode());
	}

	@Test
	void 장바구니_삭제() {
		// given
		final Integer userId = 1;
		final Long bucketId = 1L;
		Bucket bucket = new Bucket(
				bucketId,
				userId,
				"sellerName",
				101,
				10,
				LocalDate.of(2024, 5, 28)
		);
		given(bucketRepository.findByIdAndUserId(anyLong(), anyInt()))
				.willReturn(bucket);
		final ArgumentCaptor<Bucket> captor = ArgumentCaptor.forClass(Bucket.class);

		// when
		BucketDto bucketDto = bucketDomainService.deleteBucket(anyInt(), anyLong());

		// then
		verify(bucketRepository, times(1)).delete(captor.capture());
		assertEquals(bucketDto.id(), captor.getValue().getId());
		assertEquals(bucketDto.userId(), captor.getValue().getUserId());
		assertEquals(bucketDto.seller(), captor.getValue().getSeller());
		assertEquals(bucketDto.productId(), captor.getValue().getProductId());
		assertEquals(bucketDto.quantity(), captor.getValue().getQuantity());
	}

	@Test
	void 장바구니_리스트_삭제() {
		// given
		List<Bucket> buckets = List.of(
				new Bucket(
						1L,
						1,
						"sellerName",
						101,
						10,
						LocalDate.of(2024, 5, 28)

				),
				new Bucket(
						2L,
						1,
						"sellerName",
						102,
						20,
						LocalDate.of(2024, 5, 28)

				),
				new Bucket(
						3L,
						1,
						"sellerName",
						103,
						30,
						LocalDate.of(2024, 5, 28)

				)
		);
		given(bucketRepository.findAllByInId(anyList()))
				.willReturn(buckets);

		// when
		bucketDomainService.deletedBuckets(anyList());

		// then
		verify(bucketRepository, times(1)).deleteAll(buckets);
	}

}
