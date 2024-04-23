
package org.ecommerce.paymentapi.controller;

import static org.ecommerce.userapi.entity.type.Role.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.dto.BeanPayMapper;
import org.ecommerce.paymentapi.dto.TossDto;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.BeanPayDetail;
import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.ecommerce.paymentapi.entity.type.ProcessStatus;
import org.ecommerce.paymentapi.service.BeanPayService;
import org.ecommerce.paymentapi.service.PaymentServiceImpl;
import org.ecommerce.paymentapi.utils.BeanPayTimeFormatUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BeanPayController.class)
@MockBean(JpaMetamodelMappingContext.class)
class BeanPayDetailControllerTest {

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

	@BeforeEach
	void setup() {
		this.mvc = MockMvcBuilders.webAppContextSetup(wac)
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}

	@Test
	void 사전결제객체_생성() throws Exception {
		//given
		final BeanPayDto.Request.PreCharge request = new BeanPayDto.Request.PreCharge(1, 10_000);
		final BeanPayDetail entity = BeanPayDetail.ofCreate(getBeanPay(), 1, 10_000);
		final BeanPayDto dto = BeanPayMapper.INSTANCE.toDto(entity);

		when(beanPayService.preChargeBeanPay(request)).thenReturn(dto);
		final Response<BeanPayDto> response = new Response<>(200, dto);

		//when
		MvcResult mvcResult = mvc.perform(post("/api/beanpay/v1").contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(request))).andExpect(status().isOk()).andReturn();

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
			final Integer userId = 1;
			final String paymentType = "카드";

			final Integer amount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";

			final TossDto.Request.TossPayment request = new TossDto.Request.TossPayment(paymentType, paymentKey,
				orderId, amount);
			final BeanPayDto response = new BeanPayDto(orderId, paymentKey, userId,
				amount, paymentType, null, null,
				BeanPayStatus.DEPOSIT, ProcessStatus.COMPLETED, LocalDateTime.now(),
				BeanPayTimeFormatUtil.stringToDateTime(approveDateTime));
			final Response<BeanPayDto> result = new Response<>(200, response);

			when(beanPayService.validTossCharge(request)).thenReturn(response);

			//when
			MvcResult mvcResult = mvc.perform(get("/api/beanpay/v1/success")
					.contentType(MediaType.APPLICATION_JSON)
					.param("orderId", String.valueOf(request.orderId()))
					.param("paymentKey", request.paymentKey())
					.param("paymentType", request.paymentType())
					.param("amount", String.valueOf(request.amount())))
				.andExpect(status().isOk())
				.andReturn();

			//then
			String actual = mvcResult.getResponse().getContentAsString();
			String expect = mapper.writeValueAsString(result);

			assertEquals(expect, actual);
		}
	}

	@Test
	void 토스사전충전_실패() throws Exception {
		//given
		final UUID orderId = UUID.randomUUID();
		final Integer userId = 1;
		final Integer amount = 1000;
		final String errorMessage = "사용자에 의해 결제가 취소되었습니다.";
		final String errorCode = "PAY_PROCESS_CANCELED";

		final BeanPayDto.Request.TossFail request = new BeanPayDto.Request.TossFail(orderId, errorCode, errorMessage);

		final BeanPayDetail entity = new BeanPayDetail(orderId, getBeanPay(), null, userId,
			amount, null,	null, errorMessage,
			BeanPayStatus.DEPOSIT, ProcessStatus.FAILED, LocalDateTime.now(), null);

		final BeanPayDto response = BeanPayMapper.INSTANCE.toDto(entity);

		final Response<BeanPayDto> result = new Response<>(200, response);

		when(beanPayService.failTossCharge(request)).thenReturn(response);

		//when
		MvcResult mvcResult = mvc.perform(get("/api/beanpay/v1/fail")
				.contentType(MediaType.APPLICATION_JSON)
				.param("orderId", String.valueOf(request.orderId()))
				.param("errorCode", request.errorCode())
				.param("errorMessage", request.errorMessage()))
			.andExpect(status().isOk())
			.andReturn();

		//then
		String actual = mvcResult.getResponse().getContentAsString();
		String expect = mapper.writeValueAsString(result);

		assertEquals(expect, actual);
	}

	private BeanPay getBeanPay() {
		return new BeanPay(1, 1, USER, 0, LocalDateTime.now());
	}

}