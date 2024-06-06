package org.ecommerce.orderapi.bucket.repository;

import org.ecommerce.orderapi.bucket.entity.Bucket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketRepository
		extends JpaRepository<Bucket, Long>, BucketCustomRepository {
}
