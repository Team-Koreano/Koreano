package org.ecommerce.orderapi.bucket.service;

import java.util.List;

import org.ecommerce.orderapi.bucket.dto.BucketDto;
import org.ecommerce.orderapi.bucket.dto.BucketMapper;
import org.ecommerce.orderapi.bucket.entity.Bucket;
import org.ecommerce.orderapi.bucket.repository.BucketRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BucketReadService {

	private final BucketRepository bucketRepository;

	/**
	 * 회원의 장바구니 목록을 조회하는 메소드입니다.
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param userId- 회원 번호
	 * @return List<BucketDto>- 장바구니 정보가 담긴 리스트
	 */
	public List<BucketDto> getAllBuckets(final Integer userId, final Pageable pageable) {

		return getPageContent(bucketRepository.findAllByUserId(userId), pageable)
				.stream()
				.map(BucketMapper.INSTANCE::toDto)
				.toList();
	}

	/**
	 * 장바구니 리스트 페이징 메소드입니다.
	 * @author ${Juwon}
	 *
	 * @param buckets- 장바구니 리스트
	 * @param pageable- 페이징 정보
	 * @return - 장바구니 리스트
	 */
	@VisibleForTesting
	public List<Bucket> getPageContent(
			final List<Bucket> buckets,
			final Pageable pageable
	) {
		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), buckets.size());
		return buckets.subList(start, end);
	}
}
