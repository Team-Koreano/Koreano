package org.ecommerce.paymentapi.dto;

import java.time.LocalDateTime;

public record SellerBeanPayDto(
	  Integer id ,
	  Integer userId ,
	  Integer amount ,
	  LocalDateTime createDateTime
) {
}
