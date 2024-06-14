package org.ecommerce.paymentapi.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.ecommerce.paymentapi.config.QueryDslConfig;
import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


@Import(QueryDslConfig.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserBeanPayRepositoryTest {
	@Autowired
	private UserBeanPayRepository beanPayRepository;

	@Nested
	class findUserBeanPayByUserIdAndRole {
		@Test
		void 성공() {
			//given
			UserBeanPay userBeanPay = getBeanPay();
			Integer userId = userBeanPay.getUserId();
			Integer beanId = userBeanPay.getId();
			Integer amount = userBeanPay.getAmount();

			beanPayRepository.save(userBeanPay);

			//when
			UserBeanPay findUserBeanPay =
				beanPayRepository.findUserBeanPayByUserId(userId);

			//then
			assertNotNull(findUserBeanPay);
			assertEquals(beanId, findUserBeanPay.getId());
			assertEquals(findUserBeanPay.getUserId(), userId);
			assertEquals(findUserBeanPay.getAmount(), amount);
		}

		@Test
		void 유저ID다름_실패() {
			//given
			UserBeanPay userBeanPay = getBeanPay();
			Integer difUserId = 10_000;

			beanPayRepository.save(userBeanPay);

			//when
			UserBeanPay findUserBeanPay = beanPayRepository.findUserBeanPayByUserId(difUserId);

			//then
			assertNull(findUserBeanPay);
		}
	}
	

	public UserBeanPay getBeanPay() {
		return new UserBeanPay(1, 1, 0, LocalDateTime.now(), null);
	}
}
