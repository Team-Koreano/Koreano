package org.ecommerce.paymentapi.internal.service;

import static org.ecommerce.paymentapi.entity.enumerate.LockName.*;
import static org.ecommerce.paymentapi.entity.enumerate.Role.*;

import java.util.LinkedList;
import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.aop.DistributedLock;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PaymentCancel;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PaymentDetailPrice;
import org.ecommerce.paymentapi.dto.PaymentDto;
import org.ecommerce.paymentapi.dto.PaymentDto.Request.PaymentPrice;
import org.ecommerce.paymentapi.dto.PaymentMapper;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.enumerate.Role;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.ecommerce.paymentapi.exception.PaymentDetailErrorCode;
import org.ecommerce.paymentapi.exception.PaymentErrorCode;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.ecommerce.paymentapi.repository.PaymentDetailRepository;
import org.ecommerce.paymentapi.repository.PaymentRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final BeanPayRepository beanPayRepository;
	private final PaymentDetailRepository paymentDetailRepository;

	/**
	 결제 진행
	 * @author 이우진
	 *
	 * @param - PaymentPrice 주문 결제 요청 객체
	 * @return - 반환 값 설명 텍스트
	 */
	@DistributedLock(
		lockName = BEANPAY,
		uniqueKey = {
			"#paymentPrice.userId() + 'USER'",
			"#paymentPrice.paymentDetails().get().sellerId() + 'SELLER'"
		}
	)
	public PaymentDto paymentPrice(final PaymentPrice paymentPrice) {
		//유저 BeanPay 가져오기
		final BeanPay userBeanPay = getBeanPay(paymentPrice.userId(), USER);

		// 판매자들 BeanPay 가져오기
		final List<BeanPay> sellerBeanPays =
			beanPayRepository.findBeanPayByUserIdsAndRole(
			paymentPrice.extractSellerIds(),
			SELLER
		);

		// 결제
		final Payment payment = Payment.ofPayment(
			userBeanPay,
			paymentPrice.orderId(),
			paymentPrice.totalAmount(),
			paymentPrice.orderName(),
			mappedBeanPayPaymentDetailPrice(paymentPrice, sellerBeanPays)
		);

		Payment save = paymentRepository.save(payment);
		return PaymentMapper.INSTANCE.paymentToDto(save);
	}

	@VisibleForTesting
	public static List<Pair<BeanPay, PaymentDetailPrice>> mappedBeanPayPaymentDetailPrice(
		final PaymentPrice paymentPrice,
		final List<BeanPay> sellerBeanPays
	) {
		final List<Pair<BeanPay, PaymentDetailPrice>> beanPayPaymentDetailPriceMap =
			new LinkedList<>();

		for (final PaymentDetailPrice detailPrice : paymentPrice.paymentDetails()) {
			beanPayPaymentDetailPriceMap.add(
				Pair.of(
					sellerBeanPays.stream()
						.filter(sellerBeanPay ->
							sellerBeanPay.getUserId().equals(detailPrice.sellerId()))
						.findFirst()
						.orElseThrow(() ->
							new CustomException(BeanPayErrorCode.NOT_FOUND_SELLER_ID)),
					detailPrice)
			);
		}
		return beanPayPaymentDetailPriceMap;
	}

	/**
	 결제 개별 취소
	 * @author 이우진
	 *
	 * @param - PaymentPrice 주문 결제 요청 객체
	 * @return - 반환 값 설명 텍스트
	 */
	@DistributedLock(
		lockName = BEANPAY,
		uniqueKey = {
		"paymentCancel.sellerId() + 'SELLER'",
		"paymentCancel.userId() + 'USER'",
	})
	public PaymentDetailDto cancelPaymentDetail(
		final PaymentCancel paymentCancel
	) {
		final PaymentDetail paymentDetail = getPaymentDetail(paymentCancel.orderItemId());

		return PaymentMapper.INSTANCE.paymentDetailToDto(
			paymentDetail.cancelPaymentDetail(paymentCancel.cancelReason())
		);
	}

	private PaymentDetail getPaymentDetail(final Long orderItemId) {
		return paymentDetailRepository.findPaymentDetailByOrderItemId(orderItemId)
			.orElseThrow(() -> new CustomException(PaymentDetailErrorCode.NOT_EXIST));
	}

	private Payment getPayment(final Long orderId) {
		return paymentRepository.findByOrderId(orderId).orElseThrow(
			() -> new CustomException(PaymentErrorCode.NOT_FOUND_ORDER_ID)
		);
	}

	private BeanPay getBeanPay(final Integer userId, final Role role) {
		return beanPayRepository.findBeanPayByUserIdAndRole(userId, role)
			.orElseThrow(() -> new CustomException(BeanPayErrorCode.NOT_FOUND_ID));
	}

}
