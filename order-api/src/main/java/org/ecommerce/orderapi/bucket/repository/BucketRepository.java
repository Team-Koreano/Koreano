package org.ecommerce.orderapi.bucket.repository;

import java.util.List;
import java.util.Optional;

import org.ecommerce.orderapi.bucket.entity.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketRepository extends JpaRepository<Bucket, Long> {

	List<Bucket> findAllByUserId(final Integer userId);

	Optional<Bucket> findByIdAndUserId(final Long bucketId, final Integer userId);

	List<Bucket> findAllByIdInAndUserId(List<Long> bucketIds, Integer userId);
}
