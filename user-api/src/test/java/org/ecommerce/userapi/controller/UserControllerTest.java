package org.ecommerce.userapi.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.assertj.core.api.Assertions;
import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.UserDto;
import org.ecommerce.userapi.entity.type.Gender;
import org.ecommerce.userapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
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

	@BeforeEach
	public void 기초_셋팅() {
		this.mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity())
			.build();
	}

	private ResultActions performPostRequest(String content) throws Exception {
		return mockMvc.perform(post("/users/register")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(content)
			.with(user("test")));
	}

	@Test
	void 회원_등록() throws Exception {
		//given
		UserDto.Request.Register registerRequest = new UserDto.Request.Register(
			"test@example.com",
			"Test User",
			"password",
			Gender.MALE,
			(short)30,
			"01012345678");

		UserDto.Response.Register expectedResponse = new UserDto.Response.Register(
			registerRequest.email(),
			registerRequest.name(),
			registerRequest.gender(),
			registerRequest.age(),
			registerRequest.phoneNumber());

		String content = objectMapper.writeValueAsString(registerRequest);

		when(userService.registerRequest(registerRequest)).thenReturn(expectedResponse);

		//when
		final ResultActions resultActions = performPostRequest(content);

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
}