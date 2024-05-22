package org.ecommerce.paymentapi.dto;

import static org.ecommerce.paymentapi.exception.BeanPayErrorMessage.*;

import java.time.LocalDateTime;

import org.ecommerce.paymentapi.entity.enumerate.Role;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
		public record CreateBeanPay(
			@NotNull(message = NOT_NULL_USER_ID)
			@Min(value = 1, message = NOT_UNDER_ONE_USER_ID)
			Integer userId,
			@NotBlank(message = NOT_BLANK_USER_ROLE)
			Role role
		) {
		}
	}

	public record Response(
		Integer id,
		Integer userId,
		Role role,
		Integer amount,
		LocalDateTime createDateTime
	) {
	}
}
