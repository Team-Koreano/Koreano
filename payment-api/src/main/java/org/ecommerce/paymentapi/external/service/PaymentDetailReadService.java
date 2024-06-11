package org.ecommerce.paymentapi.external.service;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentMapper;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.repository.PaymentDetailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentDetailReadService {
	private final PaymentDetailRepository paymentDetailRepository;

	/**
	 사용자의 빈페이 내역을 조회합니다.
	 * @author 이우진
	 *
	 * @param - 	Integer userId,
	 * @param -		LocalDateTime start,
	 * @param -		LocalDateTime end,
	 * @param -		PaymentStatus status,
	 * @param -		Pageable pageable
	 * @return - 	Page[PaymentDetailDto]
	 */
	public Page<PaymentDetailDto> getUserPaymentDetailsByBetweenDate(
		Integer userId,
		LocalDateTime start,
		LocalDateTime end,
		PaymentStatus status,
		Pageable pageable
	) {
		List<PaymentDetail> paymentDetails =
			paymentDetailRepository.findByUserIdAndBetweenCreateDateTime(
				userId, start, end, status, pageable
			);
		return new PageImpl<>(
			paymentDetails.stream()
				.map(PaymentMapper.INSTANCE::toPaymentDetailDto)
				.toList(),
			pageable,
			paymentDetailRepository.userPaymentDetailCountByUserIdAndBetweenCreateDateTime(
				userId, start, end, status));
	}

	/**
	 판매자의 빈페이 내역을 조회합니다.
	 * @author 이우진
	 *
	 * @param - 	Integer sellerId,
					LocalDateTime start,
					LocalDateTime end,
					PaymentStatus status,
					Pageable pageable
	 * @return - Page[PaymentDetailDto]
	 */
	public Page<PaymentDetailDto> getSellerPaymentDetailByBetweenRange(
		int sellerId,
		LocalDateTime start,
		LocalDateTime end,
		PaymentStatus status,
		Pageable pageable
	) {
		List<PaymentDetail> sellerPaymentDetails =
			paymentDetailRepository.findBySellerIdAndBetweenCreateDateTime(
				sellerId, start, end, status, pageable);

		return new PageImpl<>(
			sellerPaymentDetails.stream()
				.map(PaymentMapper.INSTANCE::toPaymentDetailDto)
				.toList(),
			pageable,
			paymentDetailRepository.sellerPaymentDetailCountByUserIdAndBetweenCreatedDateTime(
				sellerId, start, end, status)
		);
	}
}
