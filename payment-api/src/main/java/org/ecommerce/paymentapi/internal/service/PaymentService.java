package org.ecommerce.paymentapi.internal.service;

import static org.ecommerce.paymentapi.entity.enumerate.Role.*;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.aop.DistributedLock;
import org.ecommerce.paymentapi.dto.PaymentDto;
import org.ecommerce.paymentapi.dto.PaymentDto.Request.PaymentPrice;
import org.ecommerce.paymentapi.dto.PaymentDto.Request.PaymentRollBack;
import org.ecommerce.paymentapi.dto.PaymentMapper;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.entity.enumerate.Role;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.ecommerce.paymentapi.exception.PaymentErrorCode;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.ecommerce.paymentapi.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final BeanPayRepository beanPayRepository;

	/**
	 결제 이벤트 발생시 결제 진행
	 * TODO: 이벤트 리스너 추가 예정
	 * @author 이우진
	 *
	 * @param - PaymentPrice 주문 결제 요청 객체
	 * @return - 반환 값 설명 텍스트
	 */
	@DistributedLock(key = {
		"'BEANPAY'.concat(#paymentPrice.sellerId()).concat('SELLER')",
		"'BEANPAY'.concat(#paymentPrice.userId()).concat('USER')",
	})
	public PaymentDto paymentPrice(PaymentPrice paymentPrice) {
		//유저 BeanPay 가져오기
		BeanPay userBeanPay = getBeanPay(paymentPrice.userId(), USER);

		//판매자 BeanPay 가져오기
		BeanPay sellerBeanPay = getBeanPay(paymentPrice.userId(), SELLER);

		// 결제
		Payment payment = Payment.ofPayment(
			userBeanPay,
			sellerBeanPay,
			paymentPrice.orderId(),
			paymentPrice.totalAmount(),
			paymentPrice.orderName(),
			paymentPrice.paymentDetails()
		);

		return PaymentMapper.INSTANCE.toDto(payment);
	}

	/**
	 결제 이벤트 발생시 결제 진행
	 * TODO: 이벤트 리스너 추가 예정
	 * @author 이우진
	 *
	 * @param - PaymentPrice 주문 결제 요청 객체
	 * @return - 반환 값 설명 텍스트
	 */
	@DistributedLock(key = {
		"'BEANPAY'.concat(#paymentRollBack.sellerId()).concat('SELLER')",
		"'BEANPAY'.concat(#paymentRollBack.userId()).concat('USER')",
	})
	public PaymentDto paymentPriceRollBack(final PaymentRollBack paymentRollBack) {
		Payment payment = getPayment(paymentRollBack);
		return PaymentMapper.INSTANCE.toDto(payment.rollbackPayment());
	}

	private Payment getPayment(PaymentRollBack paymentRollBack) {
		return paymentRepository.findByOrderId(
			paymentRollBack.orderId()).orElseThrow(
			() -> new CustomException(PaymentErrorCode.NOT_FOUND_ORDER_ID)
		);
	}

	private BeanPay getBeanPay(final Integer userId, final Role role) {
		return beanPayRepository.findBeanPayByUserIdAndRole(userId, role)
			.orElseThrow(() -> new CustomException(BeanPayErrorCode.NOT_FOUND_ID));
	}

}
