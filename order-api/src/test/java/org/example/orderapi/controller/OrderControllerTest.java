package org.example.orderapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.orderapi.controller.OrderController;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrderController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private OrderService orderService;

	@Test
	void 주문하기() throws Exception{
		// given
		OrderDto orderDto = new OrderDto(
				1L,
				1,
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드려요.",
				10000,
				LocalDateTime.of(2024,4,22,0,1),
				LocalDateTime.of(2024,4,22,0,2)
		);
		when(orderService.placeOrder(anyInt(), any(OrderDto.Request.Place.class)))
				.thenReturn(orderDto);

		// when
		// then
		mockMvc.perform(post("/api/orders/v1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new OrderDto.Request.Place(
										List.of(1L,2L,3L),
										"receiveName",
										"010-777-7777",
										"동백",
										"백동",
										"빠른 배송 부탁드려요"
								)
						)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result.id").value(orderDto.getId()))
				.andExpect(jsonPath("$.result.userId").value(orderDto.getUserId()))
				.andExpect(jsonPath("$.result.receiveName").value(orderDto.getReceiveName()))
				.andExpect(jsonPath("$.result.phoneNumber").value(orderDto.getPhoneNumber()))
				.andExpect(jsonPath("$.result.address1").value(orderDto.getAddress1()))
				.andExpect(jsonPath("$.result.address2").value(orderDto.getAddress2()))
				.andExpect(jsonPath("$.result.deliveryComment").value(orderDto.getDeliveryComment()))
				.andExpect(jsonPath("$.result.totalPaymentAmount").value(orderDto.getTotalPaymentAmount()))
				.andExpect(jsonPath("$.result.orderDatetime").value(orderDto.getOrderDatetime()))
				.andDo(print());



	}
}
