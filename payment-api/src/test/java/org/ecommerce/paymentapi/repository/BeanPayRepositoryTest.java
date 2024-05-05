package org.ecommerce.paymentapi.repository;

import static org.ecommerce.paymentapi.entity.enumerate.Role.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.ecommerce.paymentapi.config.QueryDslConfig;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.enumerate.Role;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


@Import(QueryDslConfig.class)
@ActiveProfiles("test")
@DataJpaTest
public class BeanPayRepositoryTest {
	@Autowired
	private BeanPayRepository beanPayRepository;

	@Nested
	class findBeanPayByUserIdAndRole {
		@Test
		void 성공() {
			//given
			BeanPay beanPay = getBeanPay();
			Role role = beanPay.getRole();
			Integer userId = beanPay.getUserId();
			Integer beanId = beanPay.getId();
			Integer amount = beanPay.getAmount();

			beanPayRepository.save(beanPay);

			//when
			Optional<BeanPay> optionalFindBeanPay =
				beanPayRepository.findBeanPayByUserIdAndRole(
					userId, role
				);

			//then
			assertTrue(optionalFindBeanPay.isPresent());
			BeanPay findBeanPay = optionalFindBeanPay.get();
			assertEquals(beanId, findBeanPay.getId());
			assertEquals(findBeanPay.getUserId(), userId);
			assertEquals(findBeanPay.getRole(), role);
			assertEquals(findBeanPay.getAmount(), amount);
		}

		@Test
		void 유저ID다름_실패() {
			//given
			BeanPay beanPay = getBeanPay();
			Role role = beanPay.getRole();
			Integer difUserId = 10_000;

			beanPayRepository.save(beanPay);

			//when
			Optional<BeanPay> optionalFindBeanPay =
				beanPayRepository.findBeanPayByUserIdAndRole(
					difUserId, role
				);

			//then
			assertTrue(optionalFindBeanPay.isEmpty());
		}
	}
	

	public BeanPay getBeanPay() {
		return new BeanPay(1, 1, USER, 0, LocalDateTime.now());
	}
}
