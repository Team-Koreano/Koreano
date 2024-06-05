package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.enumerated.UserStatus;

public record SellerDto(
	Integer id,
	String email,
	String name,
	String password,
	String address,
	String phoneNumber,
	LocalDateTime createDatetime,
	boolean isDeleted,
	LocalDateTime updateDatetime,
	Long beanPayId,
	UserStatus userStatus,
	String accessToken
) {
}
