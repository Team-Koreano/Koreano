package org.ecommerce.userapi.dto;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.enumerated.Gender;
import org.ecommerce.userapi.entity.enumerated.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDtoTest {
	private static final LocalDateTime CREATE_DATE = LocalDateTime.now();
	private static final Users USERS = new Users(
		1,
		"temp@temp.com",
		"user",
		"password",
		Gender.MALE,
		(short)20,
		"010-0000-0000",
		CREATE_DATE,
		false,
		null,
		0L,
		UserStatus.GENERAL,
		new ArrayList<>(),
		new ArrayList<>()
	);

	@Test
	void 회원_응답() {
		//given
		final UserDto userDto = UserMapper.INSTANCE.userToDto(USERS);
		//when
		final UserDto.Response.Register register = UserMapper.INSTANCE.userDtoToResponse(userDto);
		//then
		assertThat(register.age()).isEqualTo(USERS.getAge());
		assertThat(register.email()).isEqualTo(USERS.getEmail());
		assertThat(register.name()).isEqualTo(USERS.getName());
		assertThat(register.gender()).isEqualTo(USERS.getGender());
		assertThat(register.phoneNumber()).isEqualTo(USERS.getPhoneNumber());
	}
}