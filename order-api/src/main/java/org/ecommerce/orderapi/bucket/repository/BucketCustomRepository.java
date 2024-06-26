package org.ecommerce.orderapi.bucket.repository;

import java.util.List;

import org.ecommerce.orderapi.bucket.entity.Bucket;

public interface BucketCustomRepository {
	List<Bucket> findAllByUserId(final Integer userId, final Integer pageNumber, Integer pageSize);

	Bucket findByIdAndUserId(final Long bucketId, final Integer userId);

	List<Bucket> findAllByIdInAndUserId(final List<Long> bucketIds, final Integer userId);

	List<Bucket> findAllByInId(final List<Long> bucketIds);

	Bucket findByUserIdAndProductId(final Integer userId, final Integer productId);

	Long countBucketsByUserId(final Integer userId);
}
