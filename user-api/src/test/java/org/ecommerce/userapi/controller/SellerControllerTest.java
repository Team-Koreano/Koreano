package org.ecommerce.userapi.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.assertj.core.api.Assertions;
import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.repository.SellerRepository;
import org.ecommerce.userapi.service.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(SellerController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc
class SellerControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SellerService sellerService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private SellerRepository sellerRepository;

	@BeforeEach
	public void 기초_셋팅() {
		Seller seller = Seller.ofRegister(
			"test@example.com",
			"Jane Smith",
			"test",
			"어쩌구_저쩌구",
			"010-0000-0000"
		);
		sellerRepository.save(seller);

		this.mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity())
			.build();

	}

	private ResultActions performLoginRequest(String content) throws Exception {
		return mockMvc.perform(post("/api/sellers/v1/login")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(content)
			.with(user("test")));
	}

	private ResultActions performPostRequest(String content) throws Exception {
		return mockMvc.perform(post("/api/sellers/v1")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(content)
			.with(user("test")));
	}

	@Test
	void 셀러_등록() throws Exception {
		//given
		SellerDto.Request.Register registerRequest = new SellerDto.Request.Register(
			"test@example.com",
			"Test User",
			"password",
			"homeTown",
			"010-0000-0000");

		SellerDto.Response.Register expectedResponse = new SellerDto.Response.Register(
			registerRequest.email(),
			registerRequest.name(),
			registerRequest.address(),
			registerRequest.phoneNumber());

		String content = objectMapper.writeValueAsString(registerRequest);

		when(sellerService.registerRequest(registerRequest)).thenReturn(expectedResponse);

		//when
		final ResultActions resultActions = performPostRequest(content);

		//then
		final MvcResult mvcResult = resultActions.andExpect(status().isOk())
			.andReturn();

		SellerDto.Response.Register result = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<Response<SellerDto.Response.Register>>() {
			}
		).result();

		Assertions.assertThat(result).isEqualTo(expectedResponse);

	}

	@Test
	void 셀러_로그인() throws Exception {
		// given
		final String email = "test@example.com";
		final String password = "test";
		final SellerDto.Request.Login login = new SellerDto.Request.Login(email, password);
		final String content = objectMapper.writeValueAsString(login);

		// mock service response
		final SellerDto.Response.Login expectedResponse = SellerDto.Response.Login.of("access_token");
		when(sellerService.loginRequest(login)).thenReturn(expectedResponse);

		// when
		final ResultActions resultActions = performLoginRequest(content);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(header().exists(HttpHeaders.AUTHORIZATION))
			.andExpect(header().string(HttpHeaders.AUTHORIZATION, expectedResponse.accessToken()))
			.andExpect(content().string("로그인 되었습니다"));
	}
}
//TODO : 레디스로 인해 로그아웃 테스트 추후 구현