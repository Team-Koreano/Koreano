package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.enumerated.Role;
import org.ecommerce.userapi.exception.UserErrorMessages;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BeanPayDto {
	private Integer id;
	private Integer userId;
	private Role role;
	private Integer amount;
	private LocalDateTime createDateTime;

	public static class Request {

		public record DeleteBeanPay(
			@NotNull(message = UserErrorMessages.IdIsNotNull)
			@Min(value = 1, message = UserErrorMessages.IsCanNotBeBelowZero)
			Integer beanPayId
		) {
		}
	}

}
