package org.ecommerce.orderapi.bucket.service;

import static org.ecommerce.orderapi.bucket.exception.BucketErrorCode.*;
import static org.ecommerce.orderapi.order.exception.OrderErrorCode.*;
import static org.ecommerce.orderapi.stock.exception.StockErrorCode.*;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.orderapi.bucket.dto.BucketDto;
import org.ecommerce.orderapi.bucket.dto.BucketMapper;
import org.ecommerce.orderapi.bucket.dto.request.AddBucketRequest;
import org.ecommerce.orderapi.bucket.dto.request.ModifyBucketRequest;
import org.ecommerce.orderapi.bucket.entity.Bucket;
import org.ecommerce.orderapi.bucket.repository.BucketRepository;
import org.ecommerce.orderapi.global.client.ProductServiceClient;
import org.ecommerce.orderapi.order.dto.ProductMapper;
import org.ecommerce.orderapi.order.entity.Product;
import org.ecommerce.orderapi.stock.entity.Stock;
import org.ecommerce.orderapi.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BucketDomainService {

	private final BucketRepository bucketRepository;
	private final StockRepository stockRepository;
	private final ProductServiceClient productServiceClient;

	/**
	 * 장바구니에 상품을 담는 메소드입니다.
	 * <p>
	 * 상품 유효성 검사 개발 예정
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @param addRequest- 장바구니에 담을 상품 정보
	 * @return BucketDto- 장바구니 정보
	 */
	public BucketDto addBucket(
			final Integer userId,
			final AddBucketRequest addRequest
	) {
		validateProduct(addRequest.productId());
		validateStock(addRequest.productId(), addRequest.quantity());
		return BucketMapper.INSTANCE.toDto(
				bucketRepository.save(
						Bucket.ofAdd(
								userId,
								addRequest.seller(),
								addRequest.productId(),
								addRequest.quantity()
						)
				)
		);
	}

	/**
	 * 장바구니를 수정하는 메소드입니다.
	 * <p>
	 * 1. 수정할 장바구니가 존재하는지 검증합니다.
	 * 2. 장바구니가 회원에게 유효한지 검증합니다.
	 * 3. 상품 수량을 수정합니다.
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @param bucketId- 수정할 장바구니 번호
	 * @param modifyRequest- 수량, 옵션 둥 수정 정보
	 * @return BucketDto- 수정된 장바구니 정보
	 */
	public BucketDto modifyBucket(
			final Integer userId,
			final Long bucketId,
			final ModifyBucketRequest modifyRequest
	) {
		final Bucket bucket = getBucket(userId, bucketId);
		bucket.modifyQuantity(modifyRequest.quantity());
		return BucketMapper.INSTANCE.toDto(bucket);
	}

	/**
	 * 회원의 장바구니를 가져오는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @param bucketId- 장바구니 번호
	 * @return - 장바구니
	 */
	@VisibleForTesting
	public Bucket getBucket(final Integer userId, final Long bucketId) {
		final Bucket bucket = bucketRepository.findByIdAndUserId(bucketId, userId);
		if (bucket == null) {
			throw new CustomException(NOT_FOUND_BUCKET_ID);
		}
		return bucket;
	}

	/**
	 * 상품을 검증하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param productId- 상품 번호
	 */
	@VisibleForTesting
	public void validateProduct(final Integer productId) {
		Product product = ProductMapper.INSTANCE.responseToEntity(
				productServiceClient.getProduct(productId));
		if (product.isAvailableStatus()) {
			throw new CustomException(NOT_AVAILABLE_PRODUCT);
		}
	}

	/**
	 * 재고를 검증하는 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param productId- 상품 번호
	 * @param quantity- 수량
	 */
	@VisibleForTesting
	public void validateStock(final Integer productId, final Integer quantity) {
		Stock stock = stockRepository.findStockByProductId(productId);
		if (stock == null) {
			throw new CustomException(NOT_FOUND_STOCK);
		}

		if (!stock.hasStock(quantity)) {
			throw new CustomException(INSUFFICIENT_STOCK);
		}
	}
}
