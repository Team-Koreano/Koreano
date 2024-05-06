package org.ecommerce.paymentapi.external.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.enumerate.Role;
import org.ecommerce.paymentapi.repository.BeanPayDetailRepository;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class LockTestServiceTest {


	@Autowired
	private LockTestService lockTestService;

	@Autowired
	private BeanPayRepository beanPayRepository;

	@Autowired
	private BeanPayDetailRepository beanPayDetailRepository;



	private static final Logger log = LoggerFactory.getLogger(LockTestServiceTest.class);

	private final Integer threadCount = 32;
	private Integer beanPayId = null;


	@BeforeEach
	public void before() {
		BeanPay beanPay = BeanPay.ofCreate(1, Role.USER);
		BeanPay saveBeanPay = beanPayRepository.saveAndFlush(beanPay);
		beanPayId = saveBeanPay.getId();
	}

	@AfterEach
	public void after() {
		beanPayDetailRepository.deleteAll();
		beanPayRepository.deleteAll();
	}

	@Test
	void 트랜잭션사용_LostUpdate_발생() throws InterruptedException {
		//given
		Long startTime = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);
		String lockName = "BEANPAY";
		Integer userId = 1;
		Integer totalAmount = threadCount * 5000;

		//when
		for(int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				// 분산락 적용 메소드 호출
				try {
					lockTestService.notUseLockTest(lockName, userId);
				}finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		//then
		Long endTime = System.currentTimeMillis();
		BeanPay beanPay = beanPayRepository.findById(beanPayId).get();
		log.info("Actual total amount : {}", beanPay.getAmount());
		log.info("expect total amount : {}", totalAmount);
		log.info("total Time : {}", (endTime - startTime) + "ms");
		assertNotEquals(totalAmount, beanPay.getAmount());

	}

	@Test
	void 분산락사용_성공() throws InterruptedException {
		//given
		Long startTime = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);
		String lockName = "BEANPAY";
		Integer userId = 1;
		Integer totalAmount = threadCount * 5000;

		//when
		for(int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				//		분산락 적용 메소드 호출
				try {
					lockTestService.useDistributeLock(lockName, userId);
				}finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		//then
		Long endTime = System.currentTimeMillis();
		BeanPay beanPay = beanPayRepository.findById(beanPayId).get();
		log.info("Actual total amount : {}", beanPay.getAmount());
		log.info("expect total amount : {}", totalAmount);
		log.info("total Time : {}", (endTime - startTime) + "ms");
		assertEquals(totalAmount, beanPay.getAmount());
	}

	@Test
	void 베타락사용_성공() throws InterruptedException {
		//given
		Long startTime = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);
		String lockName = "BEANPAY";
		Integer userId = 1;
		Integer totalAmount = threadCount * 5000;

		//when
		for(int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				// 분산락 적용 메소드 호출
				try {
					lockTestService.betaLockTest(lockName, userId);
				}finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		//then
		Long endTime = System.currentTimeMillis();
		BeanPay beanPay = beanPayRepository.findById(beanPayId).get();
		log.info("Actual total amount : {}", beanPay.getAmount());
		log.info("expect total amount : {}", totalAmount);
		log.info("total Time : {}", (endTime - startTime) + "ms");
		assertEquals(totalAmount, beanPay.getAmount());

	}

	@Test
	void 분산락_AOP미적용_성공() throws InterruptedException {
		//given
		Long startTime = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);
		String lockName = "BEANPAY";
		Integer userId = 1;
		Integer totalAmount = threadCount * 5000;

		//when
		for(int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				// 분산락 적용 메소드 호출
				try {
					lockTestService.notUseAopTest(lockName, userId);
				}finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		//then
		Long endTime = System.currentTimeMillis();
		BeanPay beanPay = beanPayRepository.findById(beanPayId).get();
		log.info("Actual total amount : {}", beanPay.getAmount());
		log.info("expect total amount : {}", totalAmount);
		log.info("total Time : {}", (endTime - startTime) + "ms");
		assertEquals(totalAmount, beanPay.getAmount());

	}

}
