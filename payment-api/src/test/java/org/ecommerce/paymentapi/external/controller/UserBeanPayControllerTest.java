
package org.ecommerce.paymentapi.external.controller;

import static org.ecommerce.paymentapi.utils.BeanPayTimeFormatUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDetailMapper;
import org.ecommerce.paymentapi.dto.request.PreChargeRequest;
import org.ecommerce.paymentapi.dto.request.TossFailRequest;
import org.ecommerce.paymentapi.dto.request.TossPaymentRequest;
import org.ecommerce.paymentapi.dto.response.PaymentDetailResponse;
import org.ecommerce.paymentapi.entity.UserBeanPay;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
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
class UserBeanPayControllerTest {

	private static final Logger log = LoggerFactory.getLogger(
		UserBeanPayControllerTest.class);
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
		final PreChargeRequest request = new PreChargeRequest(1, 10_000);
		final UserBeanPay userBeanPay = getUserBeanPay();
		final PaymentDetail entity = userBeanPay.beforeCharge(10000);
		final PaymentDetailDto dto = PaymentDetailMapper.INSTANCE.toDto(entity);

		when(beanPayService.beforeCharge(request)).thenReturn(dto);
		final Response<PaymentDetailResponse> response = new Response<>(200,
			PaymentDetailMapper.INSTANCE.toResponse(dto));

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
			final String paymentType = "카드";

			final Integer amount = 1000;
			final String approveDateTime = "2024-04-14T17:41:52+09:00";
			final TossPaymentRequest request = new TossPaymentRequest(paymentType, paymentKey,
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
			final Response<PaymentDetailResponse> result = new Response<>(200,
				PaymentDetailMapper.INSTANCE.toResponse(response));

			when(beanPayService.validTossCharge(request, userId)).thenReturn(response);

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

		final TossFailRequest request = new TossFailRequest(orderId, errorCode, errorMessage);
		final UserBeanPay userBeanPay = getUserBeanPay();
		PaymentDetail paymentDetail = userBeanPay.beforeCharge(amount);
		final PaymentDetailDto response = PaymentDetailMapper.INSTANCE.toDto(paymentDetail);

		final Response<PaymentDetailResponse> result = new Response<>(200,
			PaymentDetailMapper.INSTANCE.toResponse(response));

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

	private UserBeanPay getUserBeanPay() {
		return new UserBeanPay(1, 1, 0, LocalDateTime.now(), null);
	}

}