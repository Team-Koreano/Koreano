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
	public List<Bucket> findAllByUserId(
			final Integer userId,
			final Integer pageNumber,
			final Integer pageSize
	) {
		return jpaQueryFactory.selectFrom(bucket)
				.where(bucket.userId.eq(userId))
				.orderBy(bucket.createDate.desc())
				.limit(pageSize)
				.offset((long)(pageNumber - 1) * pageSize)
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

	@Override
	public List<Bucket> findAllByInId(final List<Long> bucketIds) {
		return jpaQueryFactory.selectFrom(bucket)
				.where(bucket.id.in(bucketIds))
				.fetch();
	}

	@Override
	public Bucket findByUserIdAndProductId(
			final Integer userId,
			final Integer productId
	) {
		return jpaQueryFactory.selectFrom(bucket)
				.where(bucket.userId.eq(userId),
						bucket.productId.eq(productId))
				.fetchFirst();
	}

	@Override
	public Long countBucketsByUserId(Integer userId) {
		return jpaQueryFactory
				.select(bucket.count())
				.from(bucket)
				.where(bucket.userId.eq(userId))
				.fetchOne();
	}
}
