package org.ecommerce.paymentapi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.ecommerce.paymentapi.entity.type.ProcessStatus;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BeanPayServiceTest {

	@InjectMocks
	private BeanPayService beanPayService;

	@Mock
	private BeanPayRepository beanPayRepository;

	final LocalDateTime now = LocalDateTime.now();
	final BeanPayDto.Request.PreCharge request = new BeanPayDto.Request.PreCharge(1, 10_000);
	final BeanPay entity = new BeanPay(1L, "paymentKey", 1, 10_000, BeanPayStatus.DEPOSIT, ProcessStatus.PENDING, now);
	final BeanPayDto.Response response = new BeanPayDto.Response(1L, 1, 10_000, BeanPayStatus.DEPOSIT,
		ProcessStatus.PENDING, now);

	@Test
	void 사전충전객체_생성() {

		//given
		given(beanPayRepository.save(any())).willReturn(entity);

		//when
		final BeanPayDto.Response actual = beanPayService.preChargeBeanPay(request);

		//then
		assertThat(actual).usingRecursiveComparison()
			.isEqualTo(response);
	}
}