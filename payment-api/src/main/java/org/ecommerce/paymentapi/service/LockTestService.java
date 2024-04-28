package org.ecommerce.paymentapi.service;

import java.util.concurrent.TimeUnit;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.aop.DistributedLock;
import org.ecommerce.paymentapi.aop.TimeCheck;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.BeanPayDetail;
import org.ecommerce.paymentapi.entity.type.Role;
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

	// @TimeCheck
	@DistributedLock(key = "#lockName.concat('-').concat(#userId)")
	public void useDistributeLock(String lockName, Integer userId) {
		BeanPay beanPay = getBeanPay(1, Role.USER);

		final BeanPayDetail beanPayDetail = BeanPayDetail.ofCreate(
			beanPay,
			1,
			5000
		);

		final BeanPayDetail createBeanPayDetail = beanPayDetailRepository.save(
			beanPayDetail
		);
		beanPay.chargeBeanPayDetail(createBeanPayDetail.getAmount());
	}

	@TimeCheck
	@Transactional
	public void notUseLockTest(String lockName, Integer userId) {
		BeanPay beanPay = getBeanPay(1, Role.USER);

		final BeanPayDetail beanPayDetail = BeanPayDetail.ofCreate(
			beanPay,
			1,
			5000
		);
		final BeanPayDetail createBeanPayDetail = beanPayDetailRepository.save(
			beanPayDetail
		);
		beanPay.chargeBeanPayDetail(createBeanPayDetail.getAmount());
	}

	@TimeCheck
	@Transactional
	public void betaLockTest(String lockName, Integer userId) {
		BeanPay beanPay = beanPayRepository.findBeanPayByUserIdAndRoleUseBetaLock(1,
			Role.USER);

		final BeanPayDetail beanPayDetail = BeanPayDetail.ofCreate(
			beanPay,
			1,
			5000
		);

		final BeanPayDetail createBeanPayDetail = beanPayDetailRepository.save(
			beanPayDetail
		);
		beanPay.chargeBeanPayDetail(createBeanPayDetail.getAmount());
	}

	@Transactional
	public void notUseAopTest(String lockName, Integer userId) {
		RLock lock = redissonClient.getLock("LOCK: BEANPAY-1");
		try{
			boolean available = lock.tryLock(5L, 1L, TimeUnit.SECONDS);
			if(!available){
				throw new RuntimeException();
			}
			BeanPay beanPay = beanPayRepository.findBeanPayByUserIdAndRoleUseBetaLock(1,
				Role.USER);

			final BeanPayDetail beanPayDetail = BeanPayDetail.ofCreate(
				beanPay,
				1,
				5000
			);
			final BeanPayDetail createBeanPayDetail = beanPayDetailRepository.save(
				beanPayDetail
			);
			beanPay.chargeBeanPayDetail(createBeanPayDetail.getAmount());
		} catch (InterruptedException e) {

		}finally {
			try{
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
