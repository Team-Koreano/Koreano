package org.ecommerce.paymentapi.external.service;

import static org.ecommerce.paymentapi.entity.enumerate.LockName.*;

import java.util.concurrent.TimeUnit;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.aop.AopForTransaction;
import org.ecommerce.paymentapi.aop.DistributedLock;
import org.ecommerce.paymentapi.dto.request.PaymentPriceRequest;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.ecommerce.paymentapi.repository.PaymentDetailRepository;
import org.ecommerce.paymentapi.repository.UserBeanPayRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class LockTestService {

	private final UserBeanPayRepository userBeanPayRepository;
	private final RedissonClient redissonClient;
	private final AopForTransaction aopForTransaction;
	private final PaymentDetailRepository paymentDetailRepository;

	@DistributedLock(
		lockName = USER_BEANPAY,
		keys = "#lockName + #sellerId"
	)
	public void useDistributeLock(String lockName, Integer userId) {
		UserBeanPay userBeanPay = getUserBeanPay(1);

		PaymentDetail paymentDetail = userBeanPay.beforeCharge(5000);

		paymentDetailRepository.save(paymentDetail);
		userBeanPay.chargeBeanPayDetail(paymentDetail.getPaymentAmount());
	}

	@DistributedLock(
		lockName = {USER_BEANPAY, SELLER_BEANPAY},
		keys = {
			"#paymentPrice.userId()",
			"#paymentPrice.paymentDetails().get().sellerId()"
		}
	)
	public void useMultiLockTest(PaymentPriceRequest paymentPrice) {

		UserBeanPay userBeanPay = getUserBeanPay(1);

		PaymentDetail paymentDetail = userBeanPay.beforeCharge(5000);

		paymentDetailRepository.save(paymentDetail);
		userBeanPay.chargeBeanPayDetail(paymentDetail.getPaymentAmount());
	}

	@Transactional
	public void notUseLockTest(String lockName, Integer userId) {
		UserBeanPay userBeanPay = getUserBeanPay(1);

		PaymentDetail paymentDetail = userBeanPay.beforeCharge(5000);

		paymentDetailRepository.save(paymentDetail);
		userBeanPay.chargeBeanPayDetail(paymentDetail.getPaymentAmount());
	}

	@Transactional
	public void betaLockTest(String lockName, Integer userId) {
		UserBeanPay userBeanPay = userBeanPayRepository.findUserBeanPayByUserIdUseBetaLock(1);

		PaymentDetail paymentDetail = userBeanPay.beforeCharge(5000);

		paymentDetailRepository.save(paymentDetail);
		userBeanPay.chargeBeanPayDetail(paymentDetail.getPaymentAmount());
	}

	@Transactional
	public void notUseAopTest(String lockName, Integer userId) {
		final String key = "LOCK: BEANPAY1";
		final RLock lock = redissonClient.getLock(key);
		try{
			final boolean available = lock.tryLock(5L, 3L, TimeUnit.SECONDS);
			if(!available){
				throw new RuntimeException();
			}
			log.info("락 획득 key: {}", key);
			aopForTransaction.proceed(() -> {
				final UserBeanPay userBeanPay = getUserBeanPay(1);

				PaymentDetail paymentDetail = userBeanPay.beforeCharge(5000);

				paymentDetailRepository.save(paymentDetail);
				userBeanPay.chargeBeanPayDetail(paymentDetail.getPaymentAmount());

				log.info("빈페이 총액: {}", userBeanPay.getAmount());
			});
		} catch (InterruptedException e) {

		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			try{
				log.info("락 해제 key: {}", key);
				lock.unlock();
			}catch (IllegalStateException e){

			}
		}


	}

	private UserBeanPay getUserBeanPay(final Integer userId) {
		final UserBeanPay userBeanPay = userBeanPayRepository.findUserBeanPayByUserId(userId);
		if(userBeanPay == null)
			new CustomException(BeanPayErrorCode.NOT_FOUND_ID);
		return userBeanPay;
	}
}
