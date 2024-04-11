package org.ecommerce.bucketapi.repository;

import java.util.List;

import org.ecommerce.bucketapi.entity.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketRepository extends JpaRepository<Bucket, Long> {

	List<Bucket> findAllByUserId(final Integer userId);

	boolean existsByUserIdAndProductId(final Integer userId, final Integer productId);
}
