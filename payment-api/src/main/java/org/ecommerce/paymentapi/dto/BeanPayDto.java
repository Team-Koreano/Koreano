package org.ecommerce.paymentapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.paymentapi.entity.enumerate.Role;
import org.ecommerce.paymentapi.exception.BeanPayErrorMessage;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BeanPayDto {
	private Integer id;
	private Integer userId;
	private Role role;
	private Integer amount;
	private LocalDateTime createDateTime;

	public static class Request {
		public record CreateBeanPay(
			@Min(value = 1, message = BeanPayErrorMessage.NOT_UNDER_ONE_USER_ID)
			Integer userId,
			@NotBlank(message = BeanPayErrorMessage.NOT_BLANK_USER_ROLE)
			Role role
		) {
		}
	}

	public record Response(
	) {
	}
}
