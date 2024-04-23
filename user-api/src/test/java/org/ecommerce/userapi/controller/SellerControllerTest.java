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
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.dto.SellerMapper;
import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.entity.SellerAccount;
import org.ecommerce.userapi.repository.SellerRepository;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.security.AuthDetailsService;
import org.ecommerce.userapi.service.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
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

	@MockBean
	private AuthDetailsService authDetailsService;

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
			.addFilters(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))  // 필터 추가
			.apply(springSecurity())
			.build();

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

		Seller seller = Seller.ofRegister(
			registerRequest.email(),
			registerRequest.name(),
			registerRequest.password(),
			registerRequest.address(),
			registerRequest.phoneNumber()
		);

		final SellerDto responseDto = SellerMapper.INSTANCE.toDto(seller);

		final String content = objectMapper.writeValueAsString(responseDto);
		//when
		when(sellerService.registerRequest(registerRequest)).thenReturn(responseDto);

		final ResultActions perform = mockMvc.perform(post("/api/sellers/v1")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(content)
			.with(user("test")));
		//then
		perform.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.email").value(expectedResponse.email()))
			.andExpect(jsonPath("$.result.name").value(expectedResponse.name()))
			.andExpect(jsonPath("$.result.address").value(expectedResponse.address()))
			.andExpect(jsonPath("$.result.phoneNumber").value(expectedResponse.phoneNumber()))
			.andDo(print());




	}

	@Test
	void 셀러_로그인() throws Exception {
		// given
		final String email = "test@example.com";
		final String password = "test";
		final SellerDto.Request.Login login = new SellerDto.Request.Login(email, password);

		final String content = objectMapper.writeValueAsString(login);

		final SellerDto sellerDto = SellerMapper.INSTANCE.fromAccessToken("access_token");

		final SellerDto.Response.Login expectedResponse = SellerDto.Response.Login.of(sellerDto);


		when(sellerService.loginRequest(login)).thenReturn(sellerDto);

		// when
		final ResultActions resultActions = mockMvc.perform(post("/api/sellers/v1/login")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(content)
			.with(user("test")));

		// then
		final MvcResult mvcResult = resultActions.andExpect(status().isOk())
			.andReturn();

		SellerDto.Response.Login result = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<Response<SellerDto.Response.Login>>() {
			}
		).result();
		Assertions.assertThat(result).isEqualTo(expectedResponse);
	}
	@Test
	@WithMockUser(username = "test@example.com", roles = "SELLER")
	void 셀러_계좌_등록() throws Exception {
		// given
		final String email = "test@example.com";
		final AuthDetails authDetails = new AuthDetails(1, email, null);
		final AccountDto.Request.Register registerRequest = new AccountDto.Request.Register(
			"213124124123", "부산은행");

		final Seller seller = Seller.ofRegister(
			"test@example.com",
			"Jane Smith",
			"test",
			"부산시 사하구",
			"01087654321"
		);

		final SellerAccount account = SellerAccount.ofRegister(seller, registerRequest.number(), registerRequest.number());

		AccountDto dto = AccountMapper.INSTANCE.toDto(account);

		final AccountDto.Response.Register expectedResponse = AccountDto.Response.Register.of(dto);

		when(authDetailsService.getSellerAuth(email)).thenReturn(authDetails);
		when(sellerService.registerAccount(authDetails, registerRequest)).thenReturn(dto);

		// when
		final ResultActions resultActions = mockMvc.perform(post("/api/sellers/v1/account")
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