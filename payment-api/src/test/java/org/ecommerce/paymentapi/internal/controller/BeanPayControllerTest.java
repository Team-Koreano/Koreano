
package org.ecommerce.paymentapi.internal.controller;

import static org.ecommerce.paymentapi.entity.enumerate.Role.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.dto.BeanPayMapper;
import org.ecommerce.paymentapi.dto.request.CreateBeanPayRequest;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.enumerate.Role;
import org.ecommerce.paymentapi.internal.service.BeanPayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BeanPayController.class)
@MockBean(JpaMetamodelMappingContext.class)
class BeanPayControllerTest {

	@MockBean
	private BeanPayService beanPayService;

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

	@Nested
	class 빈페이_생성 {
		@Test
		void 성공() throws Exception {
			//given
			final Integer userId = 1;
			final Role role = USER;
			final CreateBeanPayRequest request = new CreateBeanPayRequest(userId, role);
			final BeanPay beanPay = BeanPay.ofCreate(userId, role);
			final BeanPayDto beanPayDto = BeanPayMapper.INSTANCE.toDto(beanPay);

			//when
			doNothing().when(beanPayService).createBeanPay(request);

			//then
			mvc.perform(post("/api/internal/beanpay/v1")
					.contentType(APPLICATION_JSON)
					.content(mapper.writeValueAsString(request)))
				.andDo(print())
				.andExpect(status().isOk());
		}
	}

}