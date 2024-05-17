package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.enumerated.Role;

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
			Integer userId,
			Role role
		) {
		}
	}

}
