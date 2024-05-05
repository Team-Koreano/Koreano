package org.ecommerce.paymentapi.service;

import java.util.concurrent.TimeUnit;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.aop.AopForTransaction;
import org.ecommerce.paymentapi.aop.DistributedLock;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.BeanPayDetail;
import org.ecommerce.paymentapi.entity.enumerate.Role;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.ecommerce.paymentapi.repository.BeanPayDetailRepository;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
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

	private final BeanPayRepository beanPayRepository;
	private final BeanPayDetailRepository beanPayDetailRepository;
	private final RedissonClient redissonClient;
	private final AopForTransaction aopForTransaction;

	@DistributedLock(key = "#lockName.concat('-').concat(#userId)")
	public void useDistributeLock(String lockName, Integer userId) {
		BeanPay beanPay = getBeanPay(1, Role.USER);

		BeanPayDetail beanPayDetail = beanPay.preCharge(5000);

		beanPayDetailRepository.save(beanPayDetail);
		beanPay.chargeBeanPayDetail(beanPayDetail.getAmount());
	}

	@Transactional
	public void notUseLockTest(String lockName, Integer userId) {
		BeanPay beanPay = getBeanPay(1, Role.USER);

		BeanPayDetail beanPayDetail = beanPay.preCharge(5000);

		beanPayDetailRepository.save(beanPayDetail);
		beanPay.chargeBeanPayDetail(beanPayDetail.getAmount());
	}

	@Transactional
	public void betaLockTest(String lockName, Integer userId) {
		BeanPay beanPay = beanPayRepository.findBeanPayByUserIdAndRoleUseBetaLock(1,
			Role.USER);

		BeanPayDetail beanPayDetail = beanPay.preCharge(5000);

		final BeanPayDetail createBeanPayDetail = beanPayDetailRepository.save(
			beanPayDetail
		);
		beanPay.chargeBeanPayDetail(createBeanPayDetail.getAmount());
	}

	@Transactional
	public void notUseAopTest(String lockName, Integer userId) {
		final String key = "LOCK: BEANPAY-1";
		final RLock lock = redissonClient.getLock(key);
		try{
			final boolean available = lock.tryLock(5L, 3L, TimeUnit.SECONDS);
			if(!available){
				throw new RuntimeException();
			}
			log.info("락 획득 key: {}", key);
			aopForTransaction.proceed(() -> {
				final BeanPay beanPay = getBeanPay(1,Role.USER);

				BeanPayDetail beanPayDetail = beanPay.preCharge(5000);

				beanPayDetailRepository.save(beanPayDetail);
				beanPay.chargeBeanPayDetail(beanPayDetail.getAmount());
				log.info("빈페이 총액: {}", beanPay.getAmount());
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

	private BeanPay getBeanPay(final Integer userId, final Role role) {
		return beanPayRepository.findBeanPayByUserIdAndRole(userId, role)
			.orElseThrow(() -> new CustomException(BeanPayErrorCode.NOT_FOUND_ID));
	}
}
