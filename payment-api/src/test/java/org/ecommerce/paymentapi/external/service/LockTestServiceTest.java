package org.ecommerce.paymentapi.external.service;

import static org.ecommerce.paymentapi.entity.enumerate.Role.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ecommerce.paymentapi.dto.PaymentDto.Request.PaymentPrice;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.internal.service.PaymentService;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.ecommerce.paymentapi.repository.PaymentDetailRepository;
import org.ecommerce.paymentapi.repository.PaymentRepository;
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
	private PaymentRepository paymentRepository;

	@Autowired
	private PaymentDetailRepository paymentDetailRepository;

	@Autowired
	private PaymentService paymentService;




	private static final Logger log = LoggerFactory.getLogger(LockTestServiceTest.class);

	private final Integer threadCount = 32;
	private final Integer startMoney = 1_000_000;
	private Integer beanPayId = null;
	private BeanPay userBeanPay = null;


	@BeforeEach
	public void before() {
		BeanPay userBeanPay = BeanPay.ofCreate(1, USER);
		BeanPay sellerBeanPay = new BeanPay(null, 1, SELLER, 0, null);
		BeanPay sellerBeanPay2 = new BeanPay(null, 2, SELLER, 0, null);
		this.userBeanPay = beanPayRepository.saveAndFlush(userBeanPay);
		beanPayRepository.saveAndFlush(sellerBeanPay);
		beanPayRepository.saveAndFlush(sellerBeanPay2);
		beanPayId = userBeanPay.getId();
	}

	@AfterEach
	public void after() {
		paymentDetailRepository.deleteAll();
		paymentRepository.deleteAll();
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
		log.info("Actual total chargeAmount : {}", beanPay.getAmount());
		log.info("expect total chargeAmount : {}", totalAmount);
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
		log.info("Actual total chargeAmount : {}", beanPay.getAmount());
		log.info("expect total chargeAmount : {}", totalAmount);
		log.info("total Time : {}", (endTime - startTime) + "ms");
		assertEquals(totalAmount, beanPay.getAmount());
	}

	@Test
	void 멀티분산락_성공() throws InterruptedException {
		//given
		Long startTime = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);
		Integer totalAmount = threadCount * 5000;
		PaymentPrice paymentPrice = new PaymentPrice(
			1L,
			5000,
			1,
			"orderName",
			List.of()
		);


		//when
		for(int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				//		분산락 적용 메소드 호출
				try {
					lockTestService.useMultiLockTest(paymentPrice);
				}finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		//then
		Long endTime = System.currentTimeMillis();
		BeanPay beanPay = beanPayRepository.findById(beanPayId).get();
		log.info("Actual total chargeAmount : {}", beanPay.getAmount());
		log.info("expect total chargeAmount : {}", totalAmount);
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
		log.info("Actual total chargeAmount : {}", beanPay.getAmount());
		log.info("expect total chargeAmount : {}", totalAmount);
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
		log.info("Actual total chargeAmount : {}", beanPay.getAmount());
		log.info("expect total chargeAmount : {}", totalAmount);
		log.info("total Time : {}", (endTime - startTime) + "ms");
		assertEquals(totalAmount, beanPay.getAmount());

	}

}
