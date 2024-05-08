package org.ecommerce.userapi.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

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
import org.ecommerce.userapi.entity.enumerated.Gender;
import org.ecommerce.userapi.external.service.UserService;
import org.ecommerce.userapi.repository.AddressRepository;
import org.ecommerce.userapi.repository.UserRepository;
import org.ecommerce.userapi.repository.UsersAccountRepository;
import org.ecommerce.userapi.security.AuthDetails;
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

		UserDto responseDto = UserMapper.INSTANCE.userToDto(users);

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

		String mockAccessToken = "mocked_access_token";

		UserDto mocking = UserMapper.INSTANCE.accessTokenToDto(mockAccessToken);

		when(userService.loginRequest(any(UserDto.Request.Login.class), any(HttpServletResponse.class)))
			.thenReturn(mocking);

		UserDto.Response.Login expectedResponse = UserDto.Response.Login.of(mocking);

		// when
		final ResultActions resultActions = performLoginRequest(content);

		// then
		final MvcResult mvcResult = resultActions.andExpect(status().isOk())
			.andReturn();

		final Response<UserDto.Response.Login> responseDto = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<Response<UserDto.Response.Login>>() {
			}
		);

		UserDto.Response.Login result = responseDto.result();

		assertThat(result).isEqualTo(expectedResponse);
	}

	@Test
	void 회원_주소_등록() throws Exception {
		// given
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

		AddressDto dto = AddressMapper.INSTANCE.addressToDto(address);

		final AddressDto.Response.Register expectedResponse = AddressMapper.INSTANCE.addressDtoToResponse(dto);

		when(userService.createAddress(any(AuthDetails.class), eq(registerRequest))).thenReturn(dto);

		// when
		final ResultActions resultActions = mockMvc.perform(post("/api/users/v1/address")
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

		AccountDto dto = AccountMapper.INSTANCE.userAccountToDto(account);

		final AccountDto.Response.Register expectedResponse = AccountMapper.INSTANCE.accountDtoToResponse(dto);

		when(userService.createAccount(any(AuthDetails.class), eq(registerRequest))).thenReturn(dto);
		// when
		final ResultActions resultActions = mockMvc.perform(post("/api/users/v1/account")
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
		final UserDto.Request.Withdrawal withdrawalRequest = new UserDto.Request.Withdrawal(email, phoneNumber,
			password);

		Users user = Users.ofRegister(
			email,
			"Jane Smith",
			password,
			Gender.FEMALE,
			(short)30,
			phoneNumber
		);

		when(userRepository.findUsersByEmailAndPhoneNumber(email, phoneNumber)).thenReturn(Optional.of(user));
		// when
		final ResultActions resultActions = mockMvc.perform(delete("/api/users/v1")
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