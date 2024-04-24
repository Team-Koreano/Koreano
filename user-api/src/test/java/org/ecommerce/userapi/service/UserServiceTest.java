package org.ecommerce.userapi.service;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.dto.AccountDto;
import org.ecommerce.userapi.dto.AccountMapper;
import org.ecommerce.userapi.dto.AddressDto;
import org.ecommerce.userapi.dto.AddressMapper;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.dto.UserMapper;
import org.ecommerce.userapi.entity.Address;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.UsersAccount;
import org.ecommerce.userapi.entity.type.Gender;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.repository.AddressRepository;
import org.ecommerce.userapi.repository.UserRepository;
import org.ecommerce.userapi.repository.UsersAccountRepository;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
	private UsersAccountRepository usersAccountRepository;

	@Mock
	private AddressRepository addressRepository;

	@Mock
	private JwtUtils jwtUtils;

	@BeforeEach
	public void 기초_셋팅() {
		Users user1 = Users.ofRegister(
			"user1@example.com",
			"John Doe",
			bCryptPasswordEncoder.encode("password1"),
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
	void 회원_계좌_등록() {
		// given
		final String email = "test@example.com";
		final AuthDetails authDetails = new AuthDetails(1, email, null);
		final AccountDto.Request.Register registerRequest = new AccountDto.Request.Register(
			"213124124123", "부산은행");

		final Users users = Users.ofRegister(
			"test@example.com",
			"Jane Smith",
			"test",
			Gender.FEMALE,
			(short)30,
			"01087654321"
		);

		final UsersAccount account = UsersAccount.ofRegister(users, registerRequest.number(),
			registerRequest.bankName());

		final AccountDto dto = AccountMapper.INSTANCE.toDto(account);

		when(userRepository.findById(authDetails.getUserId())).thenReturn(java.util.Optional.of(users));
		// when
		final AccountDto result = userService.registerAccount(authDetails, registerRequest);
		Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(dto);

	}

	@Test
	void 회원_주소_등록() {
		final String email = "test@example.com";
		final AuthDetails authDetails = new AuthDetails(1, email, null);
		final AddressDto.Request.Register registerRequest = new AddressDto.Request.Register(
			"우리집", "부산시 사하구 감전동 유림아파트", "103동 302호");

		final Users users = Users.ofRegister(
			"test@example.com",
			"Jane Smith",
			"test",
			Gender.FEMALE,
			(short)30,
			"01087654321"
		);

		final Address address = Address.ofRegister(users, registerRequest.name(), registerRequest.postAddress(),
			registerRequest.detail());

		final AddressDto dto = AddressMapper.INSTANCE.toDto(address);

		when(userRepository.findById(authDetails.getUserId())).thenReturn(java.util.Optional.of(users));

		// when
		final AddressDto result = userService.registerAddress(authDetails, registerRequest);

		//then
		Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(dto);

	}

	@Nested
	class 회원_등록_API {
		@Test
		void 회원_등록_성공() {
			//given
			UserDto.Request.Register newUserRequest = new UserDto.Request.Register(
				"newuser@example.com",
				"New User",
				"newpassword",
				Gender.MALE,
				(short)40,
				"01012341234");

			//when
			when(userRepository.existsByEmailOrPhoneNumber(newUserRequest.email(),
				newUserRequest.phoneNumber())).thenReturn(
				false);

			UserDto result = userService.registerRequest(newUserRequest);

			Users savedUser = Users.ofRegister(
				newUserRequest.email(),
				newUserRequest.name(),
				bCryptPasswordEncoder.encode(newUserRequest.password()),
				newUserRequest.gender(),
				newUserRequest.age(),
				newUserRequest.phoneNumber()
			);
			UserDto expectedResult = UserMapper.INSTANCE.toDto(savedUser);

			//then
			Assertions.assertThat(UserDto.Response.Register.of(expectedResult))
				.isEqualTo(UserDto.Response.Register.of(result));
		}

		@Test
		void 회원_등록_실패_이메일_중복() {

			UserDto.Request.Register duplicatedEmailRequest = new UserDto.Request.Register(
				"user3@example.com",
				"Duplicate Email",
				"newpassword",
				Gender.MALE,
				(short)40,
				"010000000");
			//when
			when(userRepository.existsByEmailOrPhoneNumber(duplicatedEmailRequest.email(),
				duplicatedEmailRequest.phoneNumber())).thenReturn(true);

			//then
			Assertions.assertThatThrownBy(() -> userService.registerRequest(duplicatedEmailRequest))
				.isInstanceOf(CustomException.class);

		}

		@Test
		void 회원_등록_실패_전화번호_중복() {
			// given
			// 중복 전화번호 케이스
			UserDto.Request.Register duplicatedPhoneRequest = new UserDto.Request.Register(
				"newuser2@example.com",
				"Duplicate Phone",
				"newpassword",
				Gender.MALE,
				(short)40,
				"01012345678");

			// when
			when(userRepository.existsByEmailOrPhoneNumber(duplicatedPhoneRequest.email(),
				duplicatedPhoneRequest.phoneNumber())).thenReturn(true);

			// then
			Assertions.assertThatThrownBy(() -> userService.registerRequest(duplicatedPhoneRequest))
				.isInstanceOf(CustomException.class);
		}
	}

	@Nested
	class 회원_로그인_API {
		@Test
		void 회원_로그인_성공() {
			// given
			String email = "user1@example.com";
			String password = "password1";

			UserDto.Request.Login loginRequest = new UserDto.Request.Login(email, password);
			Users user = Users.ofRegister(email, "John Doe", password, Gender.MALE, (short)25, "01012345678");
			when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
			when(bCryptPasswordEncoder.matches(password, user.getPassword())).thenReturn(true);

			//when
			when(jwtUtils.createTokens(any(), any())).thenReturn("Bearer fake_token");

			//then
			UserDto response = userService.loginRequest(loginRequest);

			Assertions.assertThat(response.getAccessToken()).isEqualTo("Bearer fake_token");
		}

		@Test
		void 회원_로그인_실패_이메일_틀림() {

			//이메일이 틀릴 경우
			String incorrectEmail = "incorrect@example.com";
			String password = "password1";
			UserDto.Request.Login inCorrectEmailRequest = new UserDto.Request.Login(incorrectEmail, password);

			//then
			Assertions.assertThatThrownBy(() -> userService.loginRequest(inCorrectEmailRequest))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining(UserErrorCode.NOT_FOUND_EMAIL.getMessage());
		}

		@Test
		void 회원_로그인_실패_비밀번호_틀림() {

			//비밀번호 틀릴 경우
			String email = "user1@example.com";
			String password = "password1";
			String incorrectPassword = "incorrect";

			Users user = Users.ofRegister(email, "John Doe", password, Gender.MALE, (short)25, "01012345678");
			when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

			//then
			UserDto.Request.Login inCorrectPasswordRequest = new UserDto.Request.Login(email, incorrectPassword);
			Assertions.assertThatThrownBy(() -> userService.loginRequest(inCorrectPasswordRequest))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining(UserErrorCode.IS_NOT_MATCHED_PASSWORD.getMessage());

		}
	}
}
//TODO : 레디스로 인해 로그아웃 테스트 추후 구현