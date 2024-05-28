package org.ecommerce.orderapi.bucket.repository.impl;

import static org.ecommerce.orderapi.bucket.entity.QBucket.*;

import java.util.List;

import org.ecommerce.orderapi.bucket.entity.Bucket;
import org.ecommerce.orderapi.bucket.repository.BucketCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BucketRepositoryImpl implements BucketCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Bucket> findAllByUserId(final Integer userId) {
		return jpaQueryFactory.selectFrom(bucket)
				.where(bucket.userId.eq(userId))
				.fetch();
	}

	@Override
	public Bucket findByIdAndUserId(final Long bucketId, final Integer userId) {
		return jpaQueryFactory.selectFrom(bucket)
				.where(bucket.id.eq(bucketId),
						bucket.userId.eq(userId))
				.fetchFirst();
	}

	@Override
	public List<Bucket> findAllByIdInAndUserId(
			final List<Long> bucketIds,
			final Integer userId
	) {
		return jpaQueryFactory.selectFrom(bucket)
				.where(bucket.id.in(bucketIds),
						bucket.userId.eq(userId))
				.fetch();
	}
}
