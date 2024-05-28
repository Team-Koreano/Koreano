package org.ecommerce.orderapi.bucket.repository;

import java.util.List;

import org.ecommerce.orderapi.bucket.entity.Bucket;

public interface BucketCustomRepository {
	List<Bucket> findAllByUserId(final Integer userId);

	Bucket findByIdAndUserId(final Long bucketId, final Integer userId);

	List<Bucket> findAllByIdInAndUserId(List<Long> bucketIds, Integer userId);
}
