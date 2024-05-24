package org.ecommerce.userapi.controller;

import static org.mockito.BDDMockito.*;
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
import org.ecommerce.userapi.external.service.SellerService;
import org.ecommerce.userapi.repository.SellerRepository;
import org.ecommerce.userapi.security.AuthDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
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
			.addFilters(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))  // 필터 추가
			.apply(springSecurity())
			.build();

	}

	@Test
	void 셀러_등록() throws Exception {
		//given
		final SellerDto.Request.Register registerRequest = new SellerDto.Request.Register(
			"test@example.com",
			"Test User",
			"password",
			"homeTown",
			"010-0000-0000");

		final SellerDto.Response.Register expectedResponse = new SellerDto.Response.Register(
			registerRequest.email(),
			registerRequest.name(),
			registerRequest.address(),
			registerRequest.phoneNumber());

		final Seller seller = Seller.ofRegister(
			registerRequest.email(),
			registerRequest.name(),
			registerRequest.password(),
			registerRequest.address(),
			registerRequest.phoneNumber()
		);

		final SellerDto responseDto = SellerMapper.INSTANCE.sellerToDto(seller);

		final String content = objectMapper.writeValueAsString(responseDto);
		//when
		when(sellerService.registerRequest(registerRequest)).thenReturn(responseDto);

		final ResultActions perform = mockMvc.perform(post("/api/external/sellers/v1")
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

		final String mockAccessToken = "mocked_access_token";

		final SellerDto mocking = SellerMapper.INSTANCE.accessTokenToDto(mockAccessToken);

		when(sellerService.loginRequest(any(SellerDto.Request.Login.class), any(HttpServletResponse.class)))
			.thenReturn(mocking);

		final SellerDto.Response.Login expectedResponse = SellerDto.Response.Login.of(mocking);

		MockHttpServletResponse response = new MockHttpServletResponse();

		when(sellerService.loginRequest(login, response)).thenReturn(mocking);

		// when
		final ResultActions resultActions = mockMvc.perform(post("/api/external/sellers/v1/login")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(content));

		// then
		final MvcResult mvcResult = resultActions.andExpect(status().isOk())
			.andReturn();

		final SellerDto.Response.Login result = objectMapper.readValue(
			mvcResult.getResponse().getContentAsString(),
			new TypeReference<Response<SellerDto.Response.Login>>() {
			}
		).result();
		Assertions.assertThat(result).isEqualTo(expectedResponse);
	}

	@Test
	void 셀러_계좌_등록() throws Exception {
		// given
		final AccountDto.Request.Register registerRequest = new AccountDto.Request.Register(
			"213124124123", "부산은행");

		final Seller seller = Seller.ofRegister(
			"test@example.com",
			"Jane Smith",
			"test",
			"부산시 사하구",
			"01087654321"
		);

		final SellerAccount account = SellerAccount.ofRegister(seller, registerRequest.number(),
			registerRequest.bankName());

		final AccountDto dto = AccountMapper.INSTANCE.sellerAccountToDto(account);

		final AccountDto.Response.Register expectedResponse = AccountMapper.INSTANCE.accountDtoToResponse(dto);

		when(sellerService.registerAccount(any(AuthDetails.class), eq(registerRequest))).thenReturn(dto);

		// when
		final ResultActions resultActions = mockMvc.perform(post("/api/external/sellers/v1/account")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(registerRequest)));

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.result.number").value(expectedResponse.number()))
			.andExpect(jsonPath("$.result.bankName").value(expectedResponse.bankName()));
	}

	@Test
	void 회원_탈퇴() throws Exception {
		// given
		final String email = "test@example.com";
		final String phoneNumber = "01087654321";
		final String password = "test";
		final SellerDto.Request.Withdrawal withdrawalRequest = new SellerDto.Request.Withdrawal(email, phoneNumber,
			password);

		final Seller seller = Seller.ofRegister(
			"test@example.com",
			"Jane Smith",
			"test",
			"부산시 사하구",
			"01087654321"
		);

		when(sellerRepository.findSellerByIdAndIsDeletedIsFalse(any(Integer.class))).thenReturn(seller);
		// when
		final ResultActions resultActions = mockMvc.perform(delete("/api/external/sellers/v1")
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