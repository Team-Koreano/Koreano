package org.ecommerce.userapi.service;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.type.Gender;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.repository.UserRepository;
import org.ecommerce.userapi.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtUtils jwtUtils;

	@BeforeEach
	public void 기초_셋팅() {
		Users user1 = Users.ofRegister(
			"user1@example.com",
			"John Doe",
			bCryptPasswordEncoder.encode( "password1"),
			Gender.MALE,
			(short)25,
			"01012345678"
		);
		Users user2 = Users.ofRegister(
			"user2@example.com",
			"Jane Smith",
			"password2",
			Gender.FEMALE,
			(short)30,
			"01087654321"
		);
		Users user3 = Users.ofRegister(
			"user3@example.com",
			"Bob Johnson",
			"password3",
			Gender.MALE,
			(short)35,
			"01099876543"
		);
		userRepository.save(user1);
		userRepository.save(user2);
		userRepository.save(user3);
	}

	@Test
	void 회원_등록() {
		//given
		UserDto.Request.Register newUserRequest = new UserDto.Request.Register(
			"newuser@example.com",
			"New User",
			"newpassword",
			Gender.MALE,
			(short)40,
			"01012341234");

		//when
		when(userRepository.existsByEmail(newUserRequest.email())).thenReturn(false);
		when(userRepository.existsByPhoneNumber(newUserRequest.phoneNumber())).thenReturn(false);

		UserDto.Response.Register response = userService.registerRequest(newUserRequest);

		Users savedUser = Users.ofRegister(
			newUserRequest.email(),
			newUserRequest.name(),
			bCryptPasswordEncoder.encode(newUserRequest.password()),
			newUserRequest.gender(),
			newUserRequest.age(),
			newUserRequest.phoneNumber()
		);

		//then
		Assertions.assertThat(response).isEqualTo(UserDto.Response.Register.of(savedUser));

		// given
		// 중복 이메일 케이스

		UserDto.Request.Register duplicatedEmailRequest = new UserDto.Request.Register(
			"user3@example.com",
			"Duplicate Email",
			"newpassword",
			Gender.MALE,
			(short)40,
			"01012342345");
		//when
		when(userRepository.existsByEmail(duplicatedEmailRequest.email())).thenReturn(true);

		//then
		Assertions.assertThatThrownBy(() -> userService.registerRequest(duplicatedEmailRequest))
			.isInstanceOf(CustomException.class);

		//given
		// 중복 전화번호 케이스
		UserDto.Request.Register duplicatedPhoneRequest = new UserDto.Request.Register(
			"newuser2@example.com",
			"Duplicate Phone",
			"newpassword",
			Gender.MALE,
			(short)40,
			"01099876543");
		//when
		when(userRepository.existsByEmail(duplicatedPhoneRequest.email())).thenReturn(false);
		when(userRepository.existsByPhoneNumber(duplicatedPhoneRequest.phoneNumber())).thenReturn(true);

		//then
		Assertions.assertThatThrownBy(() -> userService.registerRequest(duplicatedPhoneRequest))
			.isInstanceOf(CustomException.class);
	}

	@Test
	void 회원_로그인() {
		// given
		String email = "user1@example.com";
		String password = "password1";

		UserDto.Request.Login loginRequest = new UserDto.Request.Login(email, password);
		Users user = Users.ofRegister(email, "John Doe", password, Gender.MALE, (short)25, "01012345678");
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(bCryptPasswordEncoder.matches(password, user.getPassword())).thenReturn(true);

		//when
		when(jwtUtils.createTokens(any(), any())).thenReturn("fake_token");

		//then
		UserDto.Response.Login response = userService.loginRequest(loginRequest);
		Assertions.assertThat(response.accessToken()).isEqualTo("Bearer fake_token");

		//이메일이 틀릴 경우
		String incorrectEmail = "incorrect@example.com";
		UserDto.Request.Login inCorrectEmailRequest = new UserDto.Request.Login(incorrectEmail, password);

		//then
		Assertions.assertThatThrownBy(() -> userService.loginRequest(inCorrectEmailRequest))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(UserErrorCode.NOT_FOUND_EMAIL.getMessage());

		//비밀번호 틀릴 경우
		String incorrectPassword = "incorrect";

		//then
		UserDto.Request.Login inCorrectPasswordRequest = new UserDto.Request.Login(email, incorrectPassword);
		Assertions.assertThatThrownBy(() -> userService.loginRequest(inCorrectPasswordRequest))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(UserErrorCode.IS_NOT_MATCHED_PASSWORD.getMessage());

	}
}
//TODO : 레디스로 인해 로그아웃 테스트 추후 구현