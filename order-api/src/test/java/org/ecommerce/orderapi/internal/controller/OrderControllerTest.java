package org.ecommerce.orderapi.internal.controller;

import static org.ecommerce.orderapi.entity.enumerated.OrderStatus.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.orderapi.dto.OrderDetailDto;
import org.ecommerce.orderapi.entity.OrderStatusHistory;
import org.ecommerce.orderapi.entity.enumerated.OrderStatus;
import org.ecommerce.orderapi.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
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
	private StockService stockService;

	@Test
	void 재고_감소() throws Exception {
		// given
		final Long orderId = 1L;
		List<OrderDetailDto> orderDetailDtos = List.of(
				new OrderDetailDto(
						1L,
						101,
						10000,
						1,
						10000,
						10000,
						"seller1",
						CLOSED,
						null,
						List.of(
								new OrderStatusHistory(
										1L,
										null,
										OrderStatus.OPEN,
										LocalDateTime.of(2024, 5, 5, 0, 0)
								),
								new OrderStatusHistory(
										3L,
										null,
										CLOSED,
										LocalDateTime.of(2024, 5, 6, 0, 0)
								)
						)
				),
				new OrderDetailDto(
						2L,
						102,
						20000,
						2,
						40000,
						40000,
						"seller2",
						CLOSED,
						null,
						List.of(
								new OrderStatusHistory(
										2L,
										null,
										OrderStatus.OPEN,
										LocalDateTime.of(2024, 5, 5, 0, 0)
								),
								new OrderStatusHistory(
										4L,
										null,
										CLOSED,
										LocalDateTime.of(2024, 5, 6, 0, 0)
								)
						)
				)
		);
		given(stockService.decreaseStocks(orderId))
				.willReturn(orderDetailDtos);

		// when
		// then
		OrderDetailDto orderDetailDto1 = orderDetailDtos.get(0);
		OrderDetailDto orderDetailDto2 = orderDetailDtos.get(1);
		mockMvc.perform(put("/api/internal/orders/v1/1/stocks"))
				.andDo(print())
				.andExpect(jsonPath("$.result.[0].id").value(orderDetailDto1.getId()))
				.andExpect(jsonPath("$.result.[0].productId")
						.value(orderDetailDto1.getProductId()))
				.andExpect(jsonPath("$.result.[0].price")
						.value(orderDetailDto1.getPrice()))
				.andExpect(jsonPath("$.result.[0].quantity")
						.value(orderDetailDto1.getQuantity()))
				.andExpect(jsonPath("$.result.[0].paymentAmount")
						.value(orderDetailDto1.getPaymentAmount()))
				.andExpect(jsonPath("$.result.[0].seller")
						.value(orderDetailDto1.getSeller()))
				.andExpect(jsonPath("$.result.[0].status")
						.value(orderDetailDto1.getStatus().toString()))
				.andExpect(jsonPath("$.result.[0].orderStatusHistories[1].changeStatus")
						.value(orderDetailDto1.getOrderStatusHistories()
								.get(1)
								.getChangeStatus().toString()))
				.andExpect(jsonPath("$.result.[1].id").value(orderDetailDto2.getId()))
				.andExpect(jsonPath("$.result.[1].productId")
						.value(orderDetailDto2.getProductId()))
				.andExpect(
						jsonPath("$.result.[1].price").value(orderDetailDto2.getPrice()))
				.andExpect(jsonPath("$.result.[1].quantity")
						.value(orderDetailDto2.getQuantity()))
				.andExpect(jsonPath("$.result.[1].paymentAmount")
						.value(orderDetailDto2.getPaymentAmount()))
				.andExpect(jsonPath("$.result.[1].seller")
						.value(orderDetailDto2.getSeller()))
				.andExpect(jsonPath("$.result.[1].status")
						.value(orderDetailDto2.getStatus().toString()))
				.andExpect(jsonPath(
						"$.result.[1].orderStatusHistories[1].changeStatus")
						.value(orderDetailDto2.getOrderStatusHistories()
								.get(1)
								.getChangeStatus().toString()));

	}
}
