package org.ecommerce.userapi.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.assertj.core.api.Assertions;
import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.dto.UserMapper;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.type.Gender;
import org.ecommerce.userapi.repository.UserRepository;
import org.ecommerce.userapi.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
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


		resultActions.andReturn().getResponse();
		//then
		final MvcResult mvcResult = resultActions.andExpect(status().isOk())
			.andReturn();

		UserDto.Response.Register result = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<Response<UserDto.Response.Register>>() {
			}
		).result();

		Assertions.assertThat(result).isEqualTo(expectedResponse);

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
}
//TODO : 레디스로 인해 로그아웃 테스트 추후 구현