package org.ecommerce.userapi.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.assertj.core.api.Assertions;
import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.service.SellerService;
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

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.apply(springSecurity())
			.build();
	}

	private ResultActions performPostRequest(String content) throws Exception {
		return mockMvc.perform(post("/seller/register")
			.with(csrf())
			.contentType(MediaType.APPLICATION_JSON)
			.content(content)
			.with(user("test")));
	}

	@Test
	void 셀러_등록() throws Exception {
		//given
		SellerDto.Request.Register registerRequest = SellerDto.Request.Register.builder()
			.email("test@example.com")
			.name("Test User")
			.password("password")
			.address("homeTown")
			.build();

		SellerDto.Response.Register expectedResponse = SellerDto.Response.Register.builder()
			.email(registerRequest.email())
			.name(registerRequest.name())
			.address(registerRequest.address())
			.phoneNumber(registerRequest.phoneNumber())
			.build();

		String content = objectMapper.writeValueAsString(registerRequest);

		when(sellerService.registerSeller(registerRequest)).thenReturn(expectedResponse);

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
}