
package org.ecommerce.paymentapi.internal.service;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.dto.request.CreateBeanPayRequest;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BeanPayService {

	private final BeanPayRepository beanPayRepository;

	/**
	 * 빈페이 생성 메소드
	 * @author 이우진
	 *
	 * @param - CreateBeanPay createBeanPay
	 * @return - BeanPayDto
	 */
	public void createBeanPay(final CreateBeanPayRequest createBeanPay) {
		BeanPay beanPay = beanPayRepository.findBeanPayByUserIdAndRole(
			createBeanPay.userId(),
			createBeanPay.role()
		);

		if (beanPay != null)
			throw new CustomException(BeanPayErrorCode.ALREADY_EXISTS);
		beanPayRepository.save(
			BeanPay.ofCreate(createBeanPay.userId(), createBeanPay.role()));
	}
}