package org.ecommerce.orderapi.bucket.service;

import org.ecommerce.orderapi.bucket.dto.BucketDto;
import org.ecommerce.orderapi.bucket.dto.BucketMapper;
import org.ecommerce.orderapi.bucket.repository.BucketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	 * @return - 장바구니 리스트
	 */
	public Page<BucketDto> getAllBuckets(
			final Integer userId,
			final Integer pageNumber,
			final Integer pageSize
	) {

		return new PageImpl<>(
				bucketRepository.findAllByUserId(userId, pageNumber, pageSize)
						.stream()
						.map(BucketMapper.INSTANCE::toDto)
						.toList(),
				PageRequest.of(pageNumber, pageSize),
				bucketRepository.countBucketsByUserId(userId)
		);
	}

}
