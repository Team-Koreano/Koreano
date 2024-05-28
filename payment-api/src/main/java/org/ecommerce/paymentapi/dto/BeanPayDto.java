package org.ecommerce.paymentapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.paymentapi.entity.enumerate.Role;

public record BeanPayDto(
	  Integer id ,
	  Integer userId ,
	  Role role ,
	  Integer amount ,
	  LocalDateTime createDateTime
) {
}
