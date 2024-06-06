package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;

import org.ecommerce.userapi.entity.enumerated.Gender;
import org.ecommerce.userapi.entity.enumerated.UserStatus;

public record UserDto(
	Integer id,
	String email,
	String name,
	String password,
	Gender gender,
	Short age,
	String phoneNumber,
	LocalDateTime createDatetime,
	boolean isDeleted,
	LocalDateTime updateDatetime,
	Integer beanPayId,
	UserStatus userStatus,
	String accessToken
) {
}
