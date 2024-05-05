package org.ecommerce.paymentapi.repository;

import static org.ecommerce.paymentapi.entity.enumerate.Role.*;

import java.time.LocalDateTime;

import org.ecommerce.paymentapi.config.QueryDslConfig;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.BeanPayDetail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(QueryDslConfig.class)
@ActiveProfiles("test")
@DataJpaTest
public class BeanPayDetailRepositoryTest {

	final private BeanPay beanPay = getBeanPay();
	final private BeanPayDetail beanPayDetail = beanPay.preCharge(5000);

	@Autowired
	private BeanPayDetailRepository beanPayDetailRepository;


	@Test
	public void 충전객체_저장() {
		//given

		//when
		beanPayDetailRepository.save(beanPayDetail);
		BeanPayDetail findBeanPayDetail = beanPayDetailRepository.findById(beanPayDetail.getId()).get();

		//then
		Assertions.assertEquals( beanPayDetail.getId(), findBeanPayDetail.getId());
	}

	private BeanPay getBeanPay() {
		return new BeanPay(1, 1, USER, 0, LocalDateTime.now());
	}
}
