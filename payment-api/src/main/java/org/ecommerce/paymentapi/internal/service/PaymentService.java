package org.ecommerce.paymentapi.internal.service;

import static org.ecommerce.paymentapi.entity.enumerate.LockName.*;

import java.util.LinkedList;
import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.aop.DistributedLock;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDtoWithDetail;
import org.ecommerce.paymentapi.dto.PaymentMapper;
import org.ecommerce.paymentapi.dto.request.PaymentCancelRequest;
import org.ecommerce.paymentapi.dto.request.PaymentDetailPriceRequest;
import org.ecommerce.paymentapi.dto.request.PaymentPriceRequest;
import org.ecommerce.paymentapi.entity.SellerBeanPay;
import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.ecommerce.paymentapi.exception.PaymentErrorCode;
import org.ecommerce.paymentapi.repository.SellerBeanPayRepository;
import org.ecommerce.paymentapi.repository.UserBeanPayRepository;
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
	private final UserBeanPayRepository beanPayRepository;
	private final SellerBeanPayRepository sellerBeanPayRepository;

	/**
	 결제 진행
	 * @author 이우진
	 *
	 * @param - PaymentPrice 주문 결제 요청 객체
	 * @return - 반환 값 설명 텍스트
	 */
	@DistributedLock(
		lockName = {USER_BEANPAY, SELLER_BEANPAY},
		keys = {
			"#paymentPrice.userId()",
			"#paymentPrice.paymentDetails().get().sellerId()"
		}
	)
	public PaymentDtoWithDetail paymentPrice(final PaymentPriceRequest paymentPrice) {
		//유저 BeanPay 가져오기
		final UserBeanPay userBeanPay = getUserBeanPay(paymentPrice.userId());

		// 판매자들 BeanPay 가져오기
		final List<SellerBeanPay> sellerBeanPays =
			sellerBeanPayRepository.findSellerBeanPayBySellerIds(
			paymentPrice.extractSellerIds()
		);

		// 결제
		final Payment payment = Payment.ofPayment(
			userBeanPay,
			paymentPrice.orderId(),
			paymentPrice.orderName(),
			mappedBeanPayPaymentDetailPrice(paymentPrice, sellerBeanPays)
		);

		Payment save = paymentRepository.save(payment);
		return PaymentMapper.INSTANCE.toPaymentWithDetailDto(save);
	}

	@VisibleForTesting
	public static List<Pair<SellerBeanPay, PaymentDetailPriceRequest>> mappedBeanPayPaymentDetailPrice(
		final PaymentPriceRequest paymentPrice,
		final List<SellerBeanPay> sellerUserBeanPays
	) {
		final List<Pair<SellerBeanPay, PaymentDetailPriceRequest>> beanPayPaymentDetailPriceMap =
			new LinkedList<>();

		for (final PaymentDetailPriceRequest detailPrice : paymentPrice.paymentDetails()) {
			beanPayPaymentDetailPriceMap.add(
				Pair.of(
					sellerUserBeanPays.stream()
						.filter(sellerBeanPay ->
							sellerBeanPay.getSellerId().equals(detailPrice.sellerId()))
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
		lockName = {USER_BEANPAY, SELLER_BEANPAY},
		keys = {
		"#paymentCancel.userId()",
		"#paymentCancel.sellerId()",
	})
	public PaymentDetailDto cancelPaymentDetail(
		final PaymentCancelRequest paymentCancel
	) {
		final Payment payment = getPayment(paymentCancel.orderId());

		return PaymentMapper.INSTANCE.toPaymentDetailDto(
			payment.cancelPaymentDetail(paymentCancel.orderItemId(), paymentCancel.cancelReason())
		);
	}

	private Payment getPayment(final Long orderId) {
		Payment payment = paymentRepository.findByOrderId(orderId);
		if(payment == null)
			new CustomException(PaymentErrorCode.NOT_FOUND_ORDER_ID);
		return payment;
	}

	private UserBeanPay getUserBeanPay(final Integer userId) {
		final UserBeanPay userBeanPay = beanPayRepository.findUserBeanPayByUserId(
			userId);
		if(userBeanPay == null)
			new CustomException(BeanPayErrorCode.NOT_FOUND_ID);
		return userBeanPay;
	}

}
