package org.ecommerce.paymentapi.repository;


import java.util.Optional;

import org.ecommerce.paymentapi.entity.BeanPay;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class BeanPayRepositoryTest {

	final BeanPay beanPay = BeanPay.ofCreate(1, 10000);
	@Autowired
	private BeanPayRepository beanPayRepository;

	@Test
	public void 충전객체_저장() {
		//given

		//when
		beanPayRepository.save(beanPay);
		BeanPay findBeanPay = beanPayRepository.findById(beanPay.getId()).get();

		//then
		Assertions.assertEquals( beanPay.getId(), findBeanPay.getId());
	}
}
