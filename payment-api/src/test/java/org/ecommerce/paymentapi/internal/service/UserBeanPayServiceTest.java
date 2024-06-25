
package org.ecommerce.paymentapi.internal.service;

import static org.ecommerce.paymentapi.exception.BeanPayErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.dto.request.CreateUserBeanPayRequest;
import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.ecommerce.paymentapi.repository.UserBeanPayRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserBeanPayServiceTest {

	@InjectMocks
	private BeanPayService beanPayService;

	@Mock
	private UserBeanPayRepository beanPayRepository;


	@Nested
	class 빈페이_생성 {
		@Test
		void 성공() {
			//given
			final Integer userId = 1;
			final Integer amount = 0;
			final LocalDateTime createDateTime = LocalDateTime.now();
			final CreateUserBeanPayRequest request = new CreateUserBeanPayRequest(userId);
			final UserBeanPay userBeanPay = new UserBeanPay(1, userId, amount, createDateTime
				, null);

			//when
			when(beanPayRepository.findUserBeanPayByUserId(request.userId())).thenReturn(null);
			when(beanPayRepository.save(any(UserBeanPay.class))).thenReturn(userBeanPay);

			//then
			assertDoesNotThrow(() -> beanPayService.createUserBeanPay(request));
		}

		@Test
		void 유저_존재() {
			//given
			final Integer userId = 1;
			final CreateUserBeanPayRequest request = new CreateUserBeanPayRequest(userId);

			//when
			when(beanPayRepository.findUserBeanPayByUserId(request.userId())).thenReturn(mock(UserBeanPay.class));

			//then
			final CustomException actual = assertThrows(CustomException.class, () -> {
				beanPayService.createUserBeanPay(request);
			});
			assertEquals(actual.getErrorCode(), ALREADY_EXISTS);
		}
	}

}