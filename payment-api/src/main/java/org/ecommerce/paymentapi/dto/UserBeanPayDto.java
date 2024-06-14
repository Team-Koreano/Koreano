package org.ecommerce.paymentapi.dto;

import java.time.LocalDateTime;

public record UserBeanPayDto(
	  Integer id ,
	  Integer userId ,
	  Integer amount ,
	  LocalDateTime createDateTime
) {
}
