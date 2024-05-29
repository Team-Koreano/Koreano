
package org.ecommerce.paymentapi.internal.service;

import static org.ecommerce.paymentapi.entity.enumerate.LockName.*;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.aop.DistributedLock;
import org.ecommerce.paymentapi.dto.DeleteBeanPayRequest;
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

	/**
	 * 빈페이 임시 삭제 메소드 입니다.
	 * @author 이우진
	 *
	 * @param - 유저의 iD, Role을 전달받습니다.
	 * @return - null
	 */
	@DistributedLock(
		lockName = BEANPAY,
		uniqueKey = {
			"#deleteBeanPay.userId() + #deleteBeanPay.role().name()"
		}
	)
	public void deleteBeanPay(DeleteBeanPayRequest deleteBeanPay) {
		BeanPay beanPay = beanPayRepository.findBeanPayByUserIdAndRole(
			deleteBeanPay.userId(),
			deleteBeanPay.role()
		);
		if (beanPay == null)
			throw new CustomException(BeanPayErrorCode.NOT_FOUND_ID);

		beanPay.delete();
	}
}