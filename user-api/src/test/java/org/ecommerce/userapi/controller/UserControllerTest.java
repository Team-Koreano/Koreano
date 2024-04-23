package org.ecommerce.userapi.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.assertj.core.api.Assertions;
import org.ecommerce.common.vo.Response;
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
import org.ecommerce.userapi.repository.UserRepository;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.security.AuthDetailsService;
import org.ecommerce.userapi.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

	private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
	private static final String AUTHORIZATION_HEADER_VALUE = "Bearer aaaaaaaa.bbbbbbbb.cccccccc";

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
	private AuthDetailsService authDetailsService;

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
			.apply(springSecurity())
			.build();
	}

	@AfterEach
	public void 초기화() {
	}

	private ResultActions performLoginRequest(String content) throws Exception {
		return mockMvc.perform(post("/api/users/v1/login")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(content)
			.with(user("test")));
	}

	@Test
	void 회원_등록() throws Exception {
		//given

		final UserDto.Request.Register registerRequest = new UserDto.Request.Register(
			"test1@example.com",
			"Test User",
			"password",
			Gender.MALE,
			(short)30,
			"01012345678");

		final UserDto.Response.Register expectedResponse = new UserDto.Response.Register(
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
		final ResultActions resultActions = mockMvc.perform(post("/api/users/v1")
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
		final UserDto.Request.Login login = new UserDto.Request.Login(email, password);
		final String content = objectMapper.writeValueAsString(login);

		final UserDto userDto = UserMapper.INSTANCE.fromAccessToken("access_token");

		final UserDto.Response.Login expectedResponse = UserDto.Response.Login.of(userDto);

		when(userService.loginRequest(login)).thenReturn(userDto);

		// when
		final ResultActions resultActions = performLoginRequest(content);

		// then

		final MvcResult mvcResult = resultActions.andExpect(status().isOk())
			.andReturn();

		UserDto.Response.Login result = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<Response<UserDto.Response.Login>>() {
			}
		).result();
		Assertions.assertThat(result).isEqualTo(expectedResponse);
	}

	@Test
	@WithMockUser(username = "test@example.com")
	void 회원_주소_등록() throws Exception {
		// given
		final String email = "test@example.com";
		final AuthDetails authDetails = new AuthDetails(1, email, null);
		final AddressDto.Request.Register registerRequest = new AddressDto.Request.Register(
			"우리집", "부산시 사하구 감전동 유림아파트", "103동 302호");

		Users users = Users.ofRegister(
			"test@example.com",
			"Jane Smith",
			"test",
			Gender.FEMALE,
			(short)30,
			"01087654321"
		);

		Address address = Address.ofRegister(users, registerRequest.name(), registerRequest.postAddress(),
			registerRequest.detail());

		AddressDto dto = AddressMapper.INSTANCE.toDto(address);

		final AddressDto.Response.Register expectedResponse = AddressDto.Response.Register.of(dto);

		when(authDetailsService.getUserAuth(email)).thenReturn(authDetails);
		when(userService.registerAddress(authDetails, registerRequest)).thenReturn(dto);

		// when
		final ResultActions resultActions = mockMvc.perform(post("/api/users/v1/address")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.with(SecurityMockMvcRequestPostProcessors.user(authDetails))
			.content(objectMapper.writeValueAsString(registerRequest)));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.name").value(expectedResponse.name()))
			.andExpect(jsonPath("$.result.postAddress").value(expectedResponse.postAddress()))
			.andExpect(jsonPath("$.result.detail").value(expectedResponse.detail()))
			.andReturn();
	}

	@Test
	@WithMockUser(username = "test@example.com")
	void 회원_계좌_등록() throws Exception {
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

		final UsersAccount account = UsersAccount.ofRegister(users, registerRequest.number(), registerRequest.number());

		AccountDto dto = AccountMapper.INSTANCE.toDto(account);

		final AccountDto.Response.Register expectedResponse = AccountDto.Response.Register.of(dto);

		when(authDetailsService.getUserAuth(email)).thenReturn(authDetails);
		when(userService.registerAccount(authDetails, registerRequest)).thenReturn(dto);

		// when
		final ResultActions resultActions = mockMvc.perform(post("/api/users/v1/account")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.with(SecurityMockMvcRequestPostProcessors.user(authDetails))
			.content(objectMapper.writeValueAsString(registerRequest)));

		//then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.number").value(expectedResponse.number()))
			.andExpect(jsonPath("$.result.bankName").value(expectedResponse.bankName()))
			.andReturn();
	}
}
//TODO : 레디스로 인해 로그아웃 테스트 추후 구현