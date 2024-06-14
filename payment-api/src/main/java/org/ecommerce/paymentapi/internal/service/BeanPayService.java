
package org.ecommerce.paymentapi.internal.service;

import static org.ecommerce.paymentapi.entity.enumerate.LockName.*;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.aop.DistributedLock;
import org.ecommerce.paymentapi.dto.request.DeleteSellerBeanPayRequest;
import org.ecommerce.paymentapi.dto.request.DeleteUserBeanPayRequest;
import org.ecommerce.paymentapi.dto.request.CreateSellerBeanPayRequest;
import org.ecommerce.paymentapi.dto.request.CreateUserBeanPayRequest;
import org.ecommerce.paymentapi.entity.SellerBeanPay;
import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.ecommerce.paymentapi.repository.SellerBeanPayRepository;
import org.ecommerce.paymentapi.repository.UserBeanPayRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BeanPayService {

	private final UserBeanPayRepository userBeanPayRepository;
	private final SellerBeanPayRepository sellerBeanPayRepository;

	/**
	 * 유저 빈페이 생성 메소드
	 * @author 이우진
	 *
	 * @param - CreateBeanPayRequest createUserBeanPay
	 * @return - void
	 */
	public void createUserBeanPay(final CreateUserBeanPayRequest createUserBeanPay) {
		UserBeanPay userBeanPay = userBeanPayRepository.findUserBeanPayByUserId(
			createUserBeanPay.userId()
		);

		if (userBeanPay != null)
			throw new CustomException(BeanPayErrorCode.ALREADY_EXISTS);
		userBeanPayRepository.save(
			UserBeanPay.ofCreate(createUserBeanPay.userId()));
	}

	/**
	 * 판매자 빈페이 생성 메소드
	 * @author 이우진
	 *
	 * @param - CreateSellerBeanPayRequest createSellerBeanPay
	 * @return - void
	 */
	public void createSellerBeanPay(CreateSellerBeanPayRequest createSellerBeanPay) {
		SellerBeanPay sellerBeanPay = sellerBeanPayRepository.findSellerBeanPayBySellerId(
			createSellerBeanPay.sellerId()
		);

		if (sellerBeanPay != null)
			throw new CustomException(BeanPayErrorCode.ALREADY_EXISTS);
		sellerBeanPayRepository.save(
			SellerBeanPay.ofCreate(createSellerBeanPay.sellerId()));
	}

	/**
	 * 빈페이 임시 삭제 메소드 입니다.
	 * @author 이우진
	 *
	 * @param - 유저의 iD을 전달받습니다.
	 * @return - null
	 */
	@DistributedLock(
		lockName = USER_BEANPAY,
		keys = {
			"#deleteUserBeanPayRequest.userId()"
		}
	)
	public void deleteUserBeanPay(DeleteUserBeanPayRequest deleteUserBeanPayRequest) {
		UserBeanPay userBeanPay = userBeanPayRepository.findUserBeanPayByUserId(
			deleteUserBeanPayRequest.userId()
		);
		if (userBeanPay == null)
			throw new CustomException(BeanPayErrorCode.NOT_FOUND_ID);

		userBeanPay.delete();
	}

	/**
	 * 빈페이 임시 삭제 메소드 입니다.
	 * @author 이우진
	 *
	 * @param - 유저의 iD, Role을 전달받습니다.
	 * @return - null
	 */
	@DistributedLock(
		lockName = SELLER_BEANPAY,
		keys = {
			"#deleteSellerBeanPayRequest.sellerId()"
		}
	)
	public void deleteSellerBeanPay(DeleteSellerBeanPayRequest deleteSellerBeanPayRequest) {
		SellerBeanPay sellerBeanPay = sellerBeanPayRepository.findSellerBeanPayBySellerId(
			deleteSellerBeanPayRequest.sellerId()
		);
		if (sellerBeanPay == null)
			throw new CustomException(BeanPayErrorCode.NOT_FOUND_ID);

		sellerBeanPay.delete();
	}
}