
package org.ecommerce.paymentapi.internal.controller;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.ecommerce.paymentapi.dto.UserBeanPayDto;
import org.ecommerce.paymentapi.dto.UserBeanPayMapper;
import org.ecommerce.paymentapi.dto.request.CreateUserBeanPayRequest;
import org.ecommerce.paymentapi.entity.UserBeanPay;
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
class UserBeanPayControllerTest {

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
			final CreateUserBeanPayRequest request = new CreateUserBeanPayRequest(userId);
			final UserBeanPay userBeanPay = UserBeanPay.ofCreate(userId);
			final UserBeanPayDto userBeanPayDto = UserBeanPayMapper.INSTANCE.toDto(userBeanPay);

			//when
			doNothing().when(beanPayService).createUserBeanPay(request);

			//then
			mvc.perform(post("/api/internal/beanpay/v1/user")
					.contentType(APPLICATION_JSON)
					.content(mapper.writeValueAsString(request)))
				.andDo(print())
				.andExpect(status().isOk());
		}
	}

}