package org.ecommerce.paymentapi.external.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ecommerce.paymentapi.dto.request.PaymentDetailPriceRequest;
import org.ecommerce.paymentapi.dto.request.PaymentPriceRequest;
import org.ecommerce.paymentapi.entity.SellerBeanPay;
import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.ecommerce.paymentapi.internal.service.PaymentService;
import org.ecommerce.paymentapi.repository.PaymentDetailRepository;
import org.ecommerce.paymentapi.repository.PaymentRepository;
import org.ecommerce.paymentapi.repository.SellerBeanPayRepository;
import org.ecommerce.paymentapi.repository.UserBeanPayRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Execution(ExecutionMode.SAME_THREAD)
@ActiveProfiles("test")
@SpringBootTest
public class LockTestServiceTest {


	@Autowired
	private LockTestService lockTestService;

	@Autowired
	private UserBeanPayRepository userBeanPayRepository;

	@Autowired
	private SellerBeanPayRepository sellerBeanPayRepository;

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
	private UserBeanPay userBeanPay = null;


	@BeforeEach
	public void before() {
		UserBeanPay userBeanPay = UserBeanPay.ofCreate(1);
		SellerBeanPay sellerUserBeanPay = new SellerBeanPay(null, 1, 0, null, null);
		SellerBeanPay sellerUserBeanPay2 = new SellerBeanPay(null, 2, 0, null, null);
		this.userBeanPay = userBeanPayRepository.saveAndFlush(userBeanPay);
		sellerBeanPayRepository.saveAndFlush(sellerUserBeanPay);
		sellerBeanPayRepository.saveAndFlush(sellerUserBeanPay2);
		beanPayId = userBeanPay.getId();
	}

	@AfterEach
	public void after() {
		paymentDetailRepository.deleteAll();
		paymentRepository.deleteAll();
		userBeanPayRepository.deleteAll();
		sellerBeanPayRepository.deleteAll();
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
		UserBeanPay userBeanPay = userBeanPayRepository.findById(beanPayId).get();
		log.info("Actual total amount : {}", userBeanPay.getAmount());
		log.info("expect total amount : {}", totalAmount);
		log.info("total Time : {}", (endTime - startTime) + "ms");
		assertNotEquals(totalAmount, userBeanPay.getAmount());

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
		UserBeanPay userBeanPay = userBeanPayRepository.findById(beanPayId).get();
		log.info("Actual total amount : {}", userBeanPay.getAmount());
		log.info("expect total amount : {}", totalAmount);
		log.info("total Time : {}", (endTime - startTime) + "ms");
		assertEquals(totalAmount, userBeanPay.getAmount());
	}

	@Test
	void 멀티분산락_성공() throws InterruptedException {
		//given
		Long startTime = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);
		Integer totalAmount = threadCount * 5000;
		PaymentPriceRequest paymentPrice = new PaymentPriceRequest(
			1L,
			1,
			"orderName",
			List.of(
				new PaymentDetailPriceRequest(1L, 1000, 3, 0, 1, "productName1"),
				new PaymentDetailPriceRequest(2L, 1000, 3, 0, 1, "productName2")
			)
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
		UserBeanPay userBeanPay = userBeanPayRepository.findById(beanPayId).get();
		log.info("Actual total amount : {}", userBeanPay.getAmount());
		log.info("expect total amount : {}", totalAmount);
		log.info("total Time : {}", (endTime - startTime) + "ms");
		assertEquals(totalAmount, userBeanPay.getAmount());
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
		UserBeanPay userBeanPay = userBeanPayRepository.findById(beanPayId).get();
		log.info("Actual total amount : {}", userBeanPay.getAmount());
		log.info("expect total amount : {}", totalAmount);
		log.info("total Time : {}", (endTime - startTime) + "ms");
		assertEquals(totalAmount, userBeanPay.getAmount());

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
		UserBeanPay userBeanPay = userBeanPayRepository.findById(beanPayId).get();
		log.info("Actual total amount : {}", userBeanPay.getAmount());
		log.info("expect total amount : {}", totalAmount);
		log.info("total Time : {}", (endTime - startTime) + "ms");
		assertEquals(totalAmount, userBeanPay.getAmount());

	}


}
