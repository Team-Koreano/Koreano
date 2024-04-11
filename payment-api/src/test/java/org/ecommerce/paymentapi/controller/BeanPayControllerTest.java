package org.ecommerce.paymentapi.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.dto.PaymentDto;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.ecommerce.paymentapi.entity.type.ProcessStatus;
import org.ecommerce.paymentapi.service.BeanPayService;
import org.ecommerce.paymentapi.service.PaymentServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

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

	final LocalDateTime now = LocalDateTime.now();
	final BeanPayDto.Request.PreCharge request = new BeanPayDto.Request.PreCharge(1, 10_000);
	final BeanPay entity = new BeanPay(1L, "paymentKey", 1, 10_000, BeanPayStatus.DEPOSIT, ProcessStatus.PENDING, now);
	final BeanPayDto.Response response = new BeanPayDto.Response(1L, 1, 10_000, BeanPayStatus.DEPOSIT,
		ProcessStatus.PENDING, now);

	@Test
	void 사전결제객체_생성() throws Exception {
		//given
		when(beanPayService.preChargeBeanPay(request))
			.thenReturn(response);
		final Response<BeanPayDto.Response> response = new Response<>(200, this.response);

		//when
		MvcResult mvcResult = mvc.perform(get("/beanpay/payments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(request)))
			.andExpect(status().isOk()).andReturn();

		//then
		String actual = mvcResult.getResponse().getContentAsString();
		String expect = mapper.writeValueAsString(response);
		assertEquals(actual, expect);
	}
}