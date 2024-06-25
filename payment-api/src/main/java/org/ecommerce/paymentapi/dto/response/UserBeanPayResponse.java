package org.ecommerce.paymentapi.dto.response;

import java.time.LocalDateTime;


public record UserBeanPayResponse(
	Integer id,
	Integer userId,
	Integer amount,
	LocalDateTime createDateTime
) {
}