
package org.ecommerce.paymentapi.external.controller;

import static org.ecommerce.paymentapi.entity.enumerate.Role.*;
import static org.ecommerce.paymentapi.utils.BeanPayTimeFormatUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PreCharge;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.TossFail;
import org.ecommerce.paymentapi.dto.PaymentDetailMapper;
import org.ecommerce.paymentapi.dto.TossDto.Request.TossPayment;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.ecommerce.paymentapi.entity.enumerate.Role;
import org.ecommerce.paymentapi.external.service.BeanPayService;
import org.ecommerce.paymentapi.external.service.LockTestService;
import org.ecommerce.paymentapi.internal.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
class BeanPayControllerTest {

	private static final Logger log = LoggerFactory.getLogger(
		BeanPayControllerTest.class);
	@MockBean
	private BeanPayService beanPayService;

	@MockBean
	private PaymentService paymentService;

	@MockBean
	private LockTestService lockTestService;

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
		final PreCharge request = new PreCharge(1, 10_000);
		final BeanPay beanPay = getBeanPay();
		final PaymentDetail entity = beanPay.beforeCharge(10000);
		final PaymentDetailDto dto = PaymentDetailMapper.INSTANCE.entityToDto(entity);

		when(beanPayService.beforeCharge(request)).thenReturn(dto);
		final Response<PaymentDetailDto.Response> response = new Response<>(200,
			PaymentDetailMapper.INSTANCE.dtoToResponse(dto));

		//when
		MvcResult mvcResult =
			mvc.perform(post("/api/external/beanpay/v1/charge").contentType(MediaType.APPLICATION_JSON)
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
			final Role role = USER;
			final String paymentType = "카드";

			final Integer amount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";
			final TossPayment request = new TossPayment(paymentType, paymentKey,
				orderId, amount);
			final PaymentDetailDto response = new PaymentDetailDto(
				orderId,
				1L,
				1,
				1,
				1L,
				0,
				0,
				amount,
				"paymentName",
				null,
				null,
				paymentKey,
				paymentType,
				PaymentStatus.DEPOSIT,
				ProcessStatus.COMPLETED,
				stringToDateTime(approveDateTime),
				null,
				null
			);
			final Response<PaymentDetailDto.Response> result = new Response<>(200,
				PaymentDetailMapper.INSTANCE.dtoToResponse(response));

			when(beanPayService.validTossCharge(request, userId, role)).thenReturn(response);

			//when
			MvcResult mvcResult = mvc.perform(get("/api/external/beanpay/v1/success")
					.contentType(MediaType.APPLICATION_JSON)
					.param("orderId", String.valueOf(request.orderId()))
					.param("paymentKey", request.paymentKey())
					.param("paymentType", request.paymentType())
					.param("chargeAmount", String.valueOf(request.chargeAmount())))
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

		final TossFail request = new TossFail(orderId, errorCode, errorMessage);
		final BeanPay beanPay = getBeanPay();
		PaymentDetail paymentDetail = beanPay.beforeCharge(amount);
		final PaymentDetailDto response = PaymentDetailMapper.INSTANCE.entityToDto(paymentDetail);

		final Response<PaymentDetailDto.Response> result = new Response<>(200,
			PaymentDetailMapper.INSTANCE.dtoToResponse(response));

		when(beanPayService.failTossCharge(request)).thenReturn(response);

		//when
		MvcResult mvcResult = mvc.perform(get("/api/external/beanpay/v1/fail")
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