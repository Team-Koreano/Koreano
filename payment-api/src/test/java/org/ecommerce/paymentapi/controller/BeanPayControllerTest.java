package org.ecommerce.paymentapi.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.service.BeanPayService;
import org.ecommerce.paymentapi.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BeanPayController.class)
@MockBean(JpaMetamodelMappingContext.class)
class BeanPayControllerTest {

	@MockBean
	private BeanPayService beanPayService;

	@MockBean
	private PaymentServiceImpl paymentService;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private WebApplicationContext wac;

	final LocalDateTime now = LocalDateTime.now();
	final BeanPayDto.Request.PreCharge request = new BeanPayDto.Request.PreCharge(1, 10_000);
	// final BeanPay entity = new BeanPay(1L, "paymentKey", 1, 10_000, BeanPayStatus.DEPOSIT, ProcessStatus.PENDING, now);
	final BeanPay entity = BeanPay.ofCreate(1, 10000);
	final BeanPayDto.Response response = BeanPayDto.Response.ofCreate(entity);


	@BeforeEach
	void setup(){
		this.mvc = MockMvcBuilders.webAppContextSetup(wac)
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}

	@Test
	void 사전결제객체_생성() throws Exception {
		//given
		when(beanPayService.preChargeBeanPay(request))
			.thenReturn(response);
		final Response<BeanPayDto.Response> response = new Response<>(200, this.response);

		//when
		MvcResult mvcResult = mvc.perform(post("/api/beanpay/v1/payments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(request)))
			.andExpect(status().isOk()).andReturn();

		//then
		String actual = mvcResult.getResponse().getContentAsString();
		String expect = mapper.writeValueAsString(response);
		assertEquals(expect, actual);
	}

	@Nested
	class 토스결제승인_검증 {
		@Test
		void 성공() throws Exception {
			//given
			final UUID orderId = UUID.randomUUID();
			final String paymentKey = "paymentKey";
			final String orderName = "orderName";
			final String paymentType = "카드";
			final String method = "카드";
			final Integer amount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";

			final BeanPayDto.Request.TossPayment request =
				new BeanPayDto.Request.TossPayment(paymentType, paymentKey, orderId, amount);
			final BeanPayDto.Response.TossPayment response =
				new BeanPayDto.Response.TossPayment(paymentType, orderName, method, amount, approveDateTime);
			final Response<BeanPayDto.Response.TossPayment> result = new Response<>(200, response);


			when(beanPayService.validTossCharge(request))
				.thenReturn(response);


			//when
			MvcResult mvcResult = mvc.perform(get("/api/beanpay/v1/success")
					.contentType(MediaType.APPLICATION_JSON)
					.param("orderId", String.valueOf(request.orderId()))
					.param("paymentKey", request.paymentKey())
					.param("paymentType", request.paymentType())
					.param("amount", String.valueOf(request.amount())))
					.andExpect(status().isOk()).andReturn();

			//then
			String actual = mvcResult.getResponse().getContentAsString();
			String expect = mapper.writeValueAsString(result);

			assertEquals(expect, actual);
		}
	}

}