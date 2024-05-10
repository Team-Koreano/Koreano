package org.ecommerce.orderapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
	private Integer id;
	private String name;

	public record Response(
			Integer id,
			String name
	) {
	}
}
