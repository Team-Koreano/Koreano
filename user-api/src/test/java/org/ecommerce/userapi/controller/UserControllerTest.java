package org.ecommerce.userapi.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.AccountDto;
import org.ecommerce.userapi.dto.AccountMapper;
import org.ecommerce.userapi.dto.AddressDto;
import org.ecommerce.userapi.dto.AddressMapper;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.dto.UserMapper;
import org.ecommerce.userapi.dto.request.CreateAccountRequest;
import org.ecommerce.userapi.dto.request.CreateAddressRequest;
import org.ecommerce.userapi.dto.request.CreateUserRequest;
import org.ecommerce.userapi.dto.request.LoginUserRequest;
import org.ecommerce.userapi.dto.request.WithdrawalUserRequest;
import org.ecommerce.userapi.dto.response.CreateAccountResponse;
import org.ecommerce.userapi.dto.response.CreateAddressResponse;
import org.ecommerce.userapi.dto.response.CreateUserResponse;
import org.ecommerce.userapi.dto.response.LoginUserResponse;
import org.ecommerce.userapi.entity.Address;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.UsersAccount;
import org.ecommerce.userapi.entity.enumerated.Gender;
import org.ecommerce.userapi.external.service.UserService;
import org.ecommerce.userapi.repository.AddressRepository;
import org.ecommerce.userapi.repository.UserRepository;
import org.ecommerce.userapi.repository.UsersAccountRepository;
import org.ecommerce.common.security.AuthDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private UsersAccountRepository usersAccountRepository;

	@MockBean
	private AddressRepository addressRepository;

	@BeforeEach
	public void 기초_셋팅() {
		Users users = Users.ofRegister(
			"test@example.com",
			"Jane Smith",
			"test",
			Gender.FEMALE,
			(short)30,
			"01087654321"
		);

		userRepository.save(users);

		this.mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.addFilters(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))  // 필터 추가
			.build();
	}

	private ResultActions performLoginRequest(String content) throws Exception {
		return mockMvc.perform(post("/api/external/users/v1/login")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(content)
			.with(user("test")));
	}

	@Test
	void 회원_등록() throws Exception {
		//given

		final CreateUserRequest registerRequest = new CreateUserRequest(
			"test1@example.com",
			"Test User",
			"password",
			Gender.MALE,
			(short)30,
			"01012345678");

		final CreateUserResponse expectedResponse = new CreateUserResponse(
			registerRequest.email(),
			registerRequest.name(),
			registerRequest.gender(),
			registerRequest.age(),
			registerRequest.phoneNumber());

		Users users = Users.ofRegister(
			registerRequest.email(),
			registerRequest.name(),
			registerRequest.password(),
			registerRequest.gender(),
			registerRequest.age(),
			registerRequest.phoneNumber()
		);

		UserDto responseDto = UserMapper.INSTANCE.toDto(users);

		when(userService.registerRequest(registerRequest)).thenReturn(responseDto);

		final String content = objectMapper.writeValueAsString(registerRequest);

		//when
		final ResultActions resultActions = mockMvc.perform(post("/api/external/users/v1")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(content)
			.with(user("test")));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.email").value(expectedResponse.email()))
			.andExpect(jsonPath("$.result.name").value(expectedResponse.name()))
			.andExpect(jsonPath("$.result.age").value(expectedResponse.age().intValue()))
			.andExpect(jsonPath("$.result.gender").value(expectedResponse.gender().getCode()))
			.andExpect(jsonPath("$.result.phoneNumber").value(expectedResponse.phoneNumber()))
			.andDo(print());

	}

	@Test
	void 회원_로그인() throws Exception {

		// given
		final String email = "test@example.com";
		final String password = "test";
		final LoginUserRequest login = new LoginUserRequest(email, password);
		final String content = objectMapper.writeValueAsString(login);

		final String mockAccessToken = "mocked_access_token";

		final UserDto mocking = UserMapper.INSTANCE.toDto(mockAccessToken);

		when(userService.loginRequest(any(LoginUserRequest.class), any(HttpServletResponse.class)))
			.thenReturn(mocking);

		final LoginUserResponse expectedResponse = LoginUserResponse.of(mocking);

		// when
		final ResultActions resultActions = performLoginRequest(content);

		// then
		final MvcResult mvcResult = resultActions.andExpect(status().isOk())
			.andReturn();

		final Response<LoginUserResponse> responseDto = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<Response<LoginUserResponse>>() {
			}
		);

		final LoginUserResponse result = responseDto.result();

		assertThat(result).isEqualTo(expectedResponse);
	}

	@Test
	void 회원_주소_등록() throws Exception {
		// given
		final CreateAddressRequest registerRequest = new CreateAddressRequest(
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

		final CreateAddressResponse expectedResponse = AddressMapper.INSTANCE.toResponse(dto);

		when(userService.createAddress(any(AuthDetails.class), eq(registerRequest))).thenReturn(dto);

		// when
		final ResultActions resultActions = mockMvc.perform(post("/api/external/users/v1/address")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(registerRequest))
			.with(user("test@example.com")));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.name").value(expectedResponse.name()))
			.andExpect(jsonPath("$.result.postAddress").value(expectedResponse.postAddress()))
			.andExpect(jsonPath("$.result.detail").value(expectedResponse.detail()))
			.andReturn();
	}

	@Test
	void 회원_계좌_등록() throws Exception {
		// given
		final CreateAccountRequest registerRequest = new CreateAccountRequest(
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

		final CreateAccountResponse expectedResponse = AccountMapper.INSTANCE.toResponse(dto);

		when(userService.createAccount(any(AuthDetails.class), eq(registerRequest))).thenReturn(dto);
		// when
		final ResultActions resultActions = mockMvc.perform(post("/api/external/users/v1/account")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(registerRequest)));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.number").value(expectedResponse.number()))
			.andExpect(jsonPath("$.result.bankName").value(expectedResponse.bankName()))
			.andReturn();
	}

	@Test
	void 회원_탈퇴() throws Exception {
		// given
		final String email = "test@example.com";
		final String phoneNumber = "01087654321";
		final String password = "test";
		final WithdrawalUserRequest withdrawalRequest = new WithdrawalUserRequest(email, phoneNumber,
			password);

		final Users user = Users.ofRegister(
			email,
			"Jane Smith",
			password,
			Gender.FEMALE,
			(short)30,
			phoneNumber
		);

		when(userRepository.findUsersByIdAndIsDeletedIsFalse(any(Integer.class))).thenReturn(user);
		// when
		final ResultActions resultActions = mockMvc.perform(delete("/api/external/users/v1")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(withdrawalRequest)));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.result").value("탈퇴에 성공하였습니다"))
			.andReturn();

	}
}
//TODO : 레디스로 인해 로그아웃 테스트 추후 구현