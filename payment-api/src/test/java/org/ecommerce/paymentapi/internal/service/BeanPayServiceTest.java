
package org.ecommerce.paymentapi.internal.service;

import static org.ecommerce.paymentapi.entity.enumerate.Role.*;
import static org.ecommerce.paymentapi.exception.BeanPayErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.dto.request.CreateBeanPayRequest;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.enumerate.Role;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.junit.jupiter.api.Nested;
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


	@Nested
	class 빈페이_생성 {
		@Test
		void 성공() {
			//given
			final Integer userId = 1;
			final Role role = USER;
			final Integer amount = 0;
			final LocalDateTime createDateTime = LocalDateTime.now();
			final CreateBeanPayRequest request = new CreateBeanPayRequest(userId, role);
			final BeanPay beanPay = new BeanPay(1, userId, role, amount, createDateTime);

			//when
			when(beanPayRepository.findBeanPayByUserIdAndRole(request.userId(),
				request.role())).thenReturn(null);
			when(beanPayRepository.save(any(BeanPay.class))).thenReturn(beanPay);
			BeanPayDto actual = beanPayService.createBeanPay(request);

			//then
			assertEquals(actual.userId(), userId);
			assertEquals(actual.role(), role);
			assertEquals(actual.amount(), amount);
			assertEquals(actual.createDateTime(), createDateTime);
		}

		@Test
		void 유저_존재() {
			//given
			final Integer userId = 1;
			final Role role = USER;
			final CreateBeanPayRequest request = new CreateBeanPayRequest(userId, role);

			//when
			when(beanPayRepository.findBeanPayByUserIdAndRole(request.userId(),
				request.role())).thenReturn(mock(BeanPay.class));

			//then
			final CustomException actual = assertThrows(CustomException.class, () -> {
				beanPayService.createBeanPay(request);
			});
			assertEquals(actual.getErrorCode(), ALREADY_EXISTS);
		}
	}

}