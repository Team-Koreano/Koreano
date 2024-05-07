package org.ecommerce.orderapi.external.controller;

import static org.ecommerce.orderapi.entity.enumerated.OrderStatus.*;
import static org.ecommerce.orderapi.exception.ErrorMessage.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.ecommerce.orderapi.dto.OrderDetailDto;
import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.service.OrderService;
import org.ecommerce.orderapi.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
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

	@MockBean
	private StockService stockService;

	@Test
	void 주문하기() throws Exception {
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
				LocalDateTime.of(2024, 4, 22, 0, 1, 0, 1),
				LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1),
				new ArrayList<>()
		);
		when(orderService.placeOrder(anyInt(), any(OrderDto.Request.Place.class)))
				.thenReturn(orderDto);

		// when
		// then
		mockMvc.perform(post("/api/external/orders/v1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new OrderDto.Request.Place(
										List.of(1L, 2L, 3L),
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
				.andExpect(
						jsonPath("$.result.receiveName").value(orderDto.getReceiveName()))
				.andExpect(
						jsonPath("$.result.phoneNumber").value(orderDto.getPhoneNumber()))
				.andExpect(jsonPath("$.result.address1").value(orderDto.getAddress1()))
				.andExpect(jsonPath("$.result.address2").value(orderDto.getAddress2()))
				.andExpect(jsonPath("$.result.deliveryComment").value(
						orderDto.getDeliveryComment()))
				.andExpect(jsonPath("$.result.totalPaymentAmount").value(
						orderDto.getTotalPaymentAmount()))
				.andDo(print());
	}

	@Test
	void 필수정보_없이_주문하기() throws Exception {
		// given
		OrderDto.Request.Place placeRequest = new OrderDto.Request.Place(
				List.of(1L, 2L, 3L),
				"receiveName",
				"010-777-7777",
				null,
				"백동",
				"빠른 배송 부탁드려요"
		);

		// when
		// then
		mockMvc.perform(post("/api/external/orders/v1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(placeRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
				.andExpect(jsonPath("$.result").value(ADDRESS1_NOT_BLANK));
	}

	@Test
	void 주문내역_조회() throws Exception {
		// given
		final Integer userId = 1;
		final Integer year = null;
		final Integer pageNumber = 1;
		List<OrderDto> orderDtos = List.of(
				new OrderDto(
						1L,
						1,
						"receiveName",
						"010-777-7777",
						"동백",
						"백동",
						"빠른 배송 부탁드려요.",
						10000,
						LocalDateTime.of(2024, 4, 22, 0, 1, 0, 1),
						LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1),
						List.of(
								new OrderDetailDto(
										1L,
										101,
										10000,
										1,
										10000,
										10000,
										"seller1",
										OPEN,
										null
								)
						)
				)
		);
		given(orderService.getOrders(userId, year, pageNumber))
				.willReturn(orderDtos);

		// when
		// then
		OrderDto orderDto = orderDtos.get(0);
		OrderDetailDto orderDetail = orderDto.getOrderDetailDtos().get(0);
		mockMvc.perform(get("/api/external/orders/v1?year=&pageNumber=1"))
				.andDo(print())
				.andExpect(jsonPath("$.result.[0].id").value(orderDto.getId()))
				.andExpect(jsonPath("$.result.[0].userId").value(orderDto.getUserId()))
				.andExpect(jsonPath("$.result.[0].receiveName")
						.value(orderDto.getReceiveName()))
				.andExpect(jsonPath("$.result.[0].address1")
						.value(orderDto.getAddress1()))
				.andExpect(jsonPath("$.result.[0].address2")
						.value(orderDto.getAddress2()))
				.andExpect(jsonPath("$.result.[0].deliveryComment")
						.value(orderDto.getDeliveryComment()))
				.andExpect(jsonPath("$.result.[0].orderDatetime")
						.value(orderDto.getOrderDatetime().toString()))
				.andExpect(jsonPath("$.result.[0].orderDetailResponses[0].status")
						.value(orderDetail.getStatus().toString()));
	}

}
