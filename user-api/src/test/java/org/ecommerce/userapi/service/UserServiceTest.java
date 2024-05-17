package org.ecommerce.userapi.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.userapi.client.UserServiceClient;
import org.ecommerce.userapi.dto.AccountDto;
import org.ecommerce.userapi.dto.AccountMapper;
import org.ecommerce.userapi.dto.AddressDto;
import org.ecommerce.userapi.dto.AddressMapper;
import org.ecommerce.userapi.dto.BeanPayDto;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.dto.UserMapper;
import org.ecommerce.userapi.entity.Address;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.UsersAccount;
import org.ecommerce.userapi.entity.enumerated.Gender;
import org.ecommerce.userapi.entity.enumerated.Role;
import org.ecommerce.userapi.entity.enumerated.UserStatus;
import org.ecommerce.userapi.exception.UserErrorCode;
import org.ecommerce.userapi.external.service.UserService;
import org.ecommerce.userapi.provider.JwtProvider;
import org.ecommerce.userapi.repository.AddressRepository;
import org.ecommerce.userapi.repository.UserRepository;
import org.ecommerce.userapi.repository.UsersAccountRepository;
import org.ecommerce.userapi.security.AuthDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
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
	private JwtProvider jwtProvider;

	@Mock
	private UserServiceClient userServiceClient;

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
		final AuthDetails authDetails = new AuthDetails(1, null);
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

		final AccountDto dto = AccountMapper.INSTANCE.userAccountToDto(account);

		when(userRepository.findUsersByIdAndIsDeletedIsFalse(authDetails.getId())).thenReturn(
			java.util.Optional.of(users));
		// when
		final AccountDto result = userService.createAccount(authDetails, registerRequest);
		assertThat(result).usingRecursiveComparison().isEqualTo(dto);

	}

	@Test
	void 회원_주소_등록() {
		final AuthDetails authDetails = new AuthDetails(1, null);
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

		final AddressDto dto = AddressMapper.INSTANCE.addressToDto(address);

		when(userRepository.findUsersByIdAndIsDeletedIsFalse(authDetails.getId())).thenReturn(
			java.util.Optional.of(users));

		// when
		final AddressDto result = userService.createAddress(authDetails, registerRequest);

		//then
		assertThat(result).usingRecursiveComparison().isEqualTo(dto);

	}

	@Nested
	class 회원_등록_API {
		@Test
		void 회원_등록_성공() {
			//given
			final UserDto.Request.Register newUserRequest = new UserDto.Request.Register(
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

			final Users entity = new Users(
				1,
				newUserRequest.email(),
				newUserRequest.name(),
				newUserRequest.password(),
				newUserRequest.gender(),
				newUserRequest.age(),
				newUserRequest.phoneNumber(),
				LocalDateTime.now(),
				false,
				LocalDateTime.now(),
				null,
				UserStatus.GENERAL,
				null,
				null
			);

			when(userRepository.save(any(Users.class))).thenReturn(entity);
			when(userServiceClient.createBeanPay(any(BeanPayDto.Request.CreateBeanPay.class))).thenReturn(
				new BeanPayDto(
					1,
					entity.getId(),
					Role.SELLER,
					0,
					null
				));

			final UserDto result = userService.registerRequest(newUserRequest);

			final Users savedUser = Users.ofRegister(
				newUserRequest.email(),
				newUserRequest.name(),
				bCryptPasswordEncoder.encode(newUserRequest.password()),
				newUserRequest.gender(),
				newUserRequest.age(),
				newUserRequest.phoneNumber()
			);
			final UserDto expectedResult = UserMapper.INSTANCE.userToDto(savedUser);

			//then
			assertThat(UserMapper.INSTANCE.userDtoToResponse(expectedResult))
				.isEqualTo(UserMapper.INSTANCE.userDtoToResponse(result));
		}

		@Test
		void 회원_등록_실패_이메일_중복() {

			final UserDto.Request.Register duplicatedEmailRequest = new UserDto.Request.Register(
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
			assertThatThrownBy(() -> userService.registerRequest(duplicatedEmailRequest))
				.isInstanceOf(CustomException.class);

		}

		@Test
		void 회원_등록_실패_전화번호_중복() {
			// given
			// 중복 전화번호 케이스
			final UserDto.Request.Register duplicatedPhoneRequest = new UserDto.Request.Register(
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
			assertThatThrownBy(() -> userService.registerRequest(duplicatedPhoneRequest))
				.isInstanceOf(CustomException.class);
		}
	}

	@Nested
	class 회원_로그인_API {
		@Test
		void 회원_로그인_성공() {
			// given
			final String email = "user1@example.com";
			final String password = "password1";
			final MockHttpServletResponse response = new MockHttpServletResponse();

			final UserDto.Request.Login loginRequest = new UserDto.Request.Login(email, password);
			final Users user = Users.ofRegister(email, "John Doe", password, Gender.MALE, (short)25, "01012345678");
			when(userRepository.findUsersByEmailAndIsDeletedIsFalse(email)).thenReturn(Optional.of(user));
			when(bCryptPasswordEncoder.matches(password, user.getPassword())).thenReturn(true);

			//when
			when(jwtProvider.createUserTokens(any(), any(), any())).thenReturn("Bearer fake_token");

			//then
			final UserDto expectedResponse = userService.loginRequest(loginRequest, response);

			assertThat(expectedResponse.getAccessToken()).isEqualTo("Bearer fake_token");
		}

		@Test
		void 회원_로그인_실패_이메일_틀림() {

			//이메일이 틀릴 경우
			final String incorrectEmail = "incorrect@example.com";
			final String password = "password1";
			MockHttpServletResponse response = new MockHttpServletResponse();

			final UserDto.Request.Login inCorrectEmailRequest = new UserDto.Request.Login(incorrectEmail, password);

			//then
			assertThatThrownBy(() -> userService.loginRequest(inCorrectEmailRequest, response))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining(UserErrorCode.NOT_FOUND_EMAIL_OR_NOT_MATCHED_PASSWORD.getMessage());
		}

		@Test
		void 회원_로그인_실패_비밀번호_틀림() {

			//비밀번호 틀릴 경우
			final String email = "user1@example.com";
			final String password = "password1";
			final String incorrectPassword = "incorrect";
			MockHttpServletResponse response = new MockHttpServletResponse();

			final Users user = Users.ofRegister(email, "John Doe", password, Gender.MALE, (short)25, "01012345678");
			when(userRepository.findUsersByEmailAndIsDeletedIsFalse(email)).thenReturn(Optional.of(user));

			//then
			final UserDto.Request.Login inCorrectPasswordRequest = new UserDto.Request.Login(email, incorrectPassword);
			assertThatThrownBy(() -> userService.loginRequest(inCorrectPasswordRequest, response))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining(UserErrorCode.IS_NOT_MATCHED_PASSWORD.getMessage());

		}

		@Test
		void 회원_로그인_실패_탈퇴_회원() {

			//비밀번호 틀릴 경우
			final String email = "user1@example.com";
			final String password = "password1";
			MockHttpServletResponse response = new MockHttpServletResponse();

			Users user = Users.ofRegister(email, "John Doe", password, Gender.MALE, (short)25, "01012345678");
			user.withdrawal();

			when(userRepository.findUsersByEmailAndIsDeletedIsFalse(email)).thenReturn(Optional.of(user));
			when(bCryptPasswordEncoder.matches(password, user.getPassword())).thenReturn(true);
			//then
			UserDto.Request.Login request = new UserDto.Request.Login(email, password);
			assertThatThrownBy(() -> userService.loginRequest(request, response))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining(UserErrorCode.IS_NOT_VALID_USER.getMessage());

		}
	}

	@Nested
	class 회원_탈퇴_API {
		@Test
		void 회원_탈퇴_성공() {
			// given
			final String email = "user1@example.com";
			final String phoneNumber = "01012345678";
			final String password = "password1";
			final AuthDetails authDetails = new AuthDetails(1, null);

			final UserDto.Request.Withdrawal withdrawalRequest = new UserDto.Request.Withdrawal(email, password,
				phoneNumber);
			final Users user = Users.ofRegister(email, "John Doe", password, Gender.MALE, (short)25, phoneNumber);
			final UsersAccount account = UsersAccount.ofRegister(user, "1234567890", "KEB하나은행");
			final Address address = Address.ofRegister(user, "집", "부산시 사하구", "123-45");

			when(userRepository.findUsersByIdAndIsDeletedIsFalse(authDetails.getId()))
				.thenReturn(Optional.of(user));

			when(bCryptPasswordEncoder.matches(password, user.getPassword())).thenReturn(true);
			// when
			userService.withdrawUser(withdrawalRequest, authDetails);

			// then
			verify(userRepository, times(1)).findUsersByIdAndIsDeletedIsFalse(authDetails.getId());

			assertThat(user.isValidStatus()).isFalse();
			assertThat(user.isDeleted()).isTrue();
		}

		@Test
		void 회원_탈퇴_실패_이메일_또는_전화번호_틀림() {
			// given
			final String incorrectEmail = "incorrect@example.com";
			final String incorrectPhoneNumber = "01011112222";
			final String password = "password1";

			final UserDto.Request.Withdrawal withdrawalRequest = new UserDto.Request.Withdrawal(incorrectEmail,
				password, incorrectPhoneNumber);
			final AuthDetails authDetails = new AuthDetails(1, null);

			// then
			assertThatThrownBy(() -> userService.withdrawUser(withdrawalRequest, authDetails))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining(UserErrorCode.NOT_FOUND_EMAIL_OR_NOT_MATCHED_PASSWORD.getMessage());
		}

		@Test
		void 회원_탈퇴_실패_비밀번호_틀림() {
			// given
			final String email = "user1@example.com";
			final String phoneNumber = "01012345678";
			final String incorrectPassword = "incorrectPassword";

			final UserDto.Request.Withdrawal withdrawalRequest = new UserDto.Request.Withdrawal(email,
				incorrectPassword, phoneNumber);
			final AuthDetails authDetails = new AuthDetails(1, null);

			final Users user = Users.ofRegister(email, "John Doe", "password1", Gender.MALE, (short)25, phoneNumber);

			when(userRepository.findUsersByIdAndIsDeletedIsFalse(authDetails.getId())).thenReturn(Optional.of(user));

			// then
			assertThatThrownBy(() -> userService.withdrawUser(withdrawalRequest, authDetails))
				.isInstanceOf(CustomException.class)
				.hasMessageContaining(UserErrorCode.NOT_FOUND_EMAIL_OR_NOT_MATCHED_PASSWORD.getMessage());
		}
	}
}
//TODO : 레디스로 인해 로그아웃 테스트 추후 구현