package org.ecommerce.orderapi.internal.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.orderapi.dto.StockDto;
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
		List<StockDto> stockDtos = List.of(
				new StockDto(
						1,
						101,
						10,
						LocalDateTime.of(2024, 5, 5, 0, 0)
				),
				new StockDto(
						2,
						102,
						20,
						LocalDateTime.of(2024, 5, 5, 0, 0)
				)
		);
		given(stockService.decreaseStocks(orderId))
				.willReturn(stockDtos);

		// when
		// then
		mockMvc.perform(put("/api/internal/orders/v1/1/stocks"))
				.andDo(print())
				.andExpect(jsonPath("$.result.[0].id").value(1))
				.andExpect(jsonPath("$.result.[0].productId").value(101))
				.andExpect(jsonPath("$.result.[0].total").value(10))
				.andExpect(jsonPath("$.result.[1].id").value(2))
				.andExpect(jsonPath("$.result.[1].productId").value(102))
				.andExpect(jsonPath("$.result.[1].total").value(20));
	}

}
