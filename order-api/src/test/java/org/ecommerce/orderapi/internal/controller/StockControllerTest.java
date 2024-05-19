// package org.ecommerce.orderapi.internal.controller;
//
// import static org.ecommerce.orderapi.entity.enumerated.OrderStatus.*;
// import static org.mockito.BDDMockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.time.LocalDateTime;
// import java.util.List;
//
// import org.ecommerce.orderapi.dto.OrderItemDto;
// import org.ecommerce.orderapi.dto.StockDto;
// import org.ecommerce.orderapi.service.StockService;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
// import org.springframework.test.web.servlet.MockMvc;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// @WebMvcTest(StockController.class)
// @MockBean(JpaMetamodelMappingContext.class)
// public class StockControllerTest {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@MockBean
// 	private StockService stockService;
//
// 	@Test
// 	void 재고_감소() throws Exception {
// 		// given
// 		final Long orderId = 1L;
// 		List<OrderItemDto> orderItemDtos = List.of(
// 				new OrderItemDto(
// 						1L,
// 						101,
// 						"productName",
// 						10000,
// 						1,
// 						10000,
// 						10000,
// 						1,
// 						"seller1",
// 						CLOSED,
// 						null,
// 						LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1)
// 				),
// 				new OrderItemDto(
// 						2L,
// 						102,
// 						"productName2",
// 						20000,
// 						2,
// 						40000,
// 						40000,
// 						2,
// 						"seller2",
// 						CLOSED,
// 						null,
// 						LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1)
// 				)
// 		);
// 		given(stockService.decreaseStocks(orderId))
// 				.willReturn(orderItemDtos);
//
// 		// when
// 		// then
// 		OrderItemDto orderItemDto1 = orderItemDtos.get(0);
// 		OrderItemDto orderItemDto2 = orderItemDtos.get(1);
// 		mockMvc.perform(put("/api/internal/stocks/v1/1/decrease"))
// 				.andDo(print())
// 				.andExpect(jsonPath("$.result.[0].id").value(orderItemDto1.getId()))
// 				.andExpect(jsonPath("$.result.[0].productId")
// 						.value(orderItemDto1.getProductId()))
// 				.andExpect(jsonPath("$.result.[0].price")
// 						.value(orderItemDto1.getPrice()))
// 				.andExpect(jsonPath("$.result.[0].quantity")
// 						.value(orderItemDto1.getQuantity()))
// 				.andExpect(jsonPath("$.result.[0].paymentAmount")
// 						.value(orderItemDto1.getPaymentAmount()))
// 				.andExpect(jsonPath("$.result.[0].sellerName")
// 						.value(orderItemDto1.getSellerName()))
// 				.andExpect(jsonPath("$.result.[0].status")
// 						.value(orderItemDto1.getStatus().toString()))
// 				.andExpect(jsonPath("$.result.[1].id").value(orderItemDto2.getId()))
// 				.andExpect(jsonPath("$.result.[1].productId")
// 						.value(orderItemDto2.getProductId()))
// 				.andExpect(
// 						jsonPath("$.result.[1].price").value(orderItemDto2.getPrice()))
// 				.andExpect(jsonPath("$.result.[1].quantity")
// 						.value(orderItemDto2.getQuantity()))
// 				.andExpect(jsonPath("$.result.[1].paymentAmount")
// 						.value(orderItemDto2.getPaymentAmount()))
// 				.andExpect(jsonPath("$.result.[1].sellerName")
// 						.value(orderItemDto2.getSellerName()))
// 				.andExpect(jsonPath("$.result.[1].status")
// 						.value(orderItemDto2.getStatus().toString()));
// 	}
//
// 	@Test
// 	void 재고_증가() throws Exception {
// 		// given
// 		final Long orderItemId = 1L;
// 		final StockDto stockDto = new StockDto(
// 				1,
// 				101,
// 				10,
// 				LocalDateTime.of(2024, 5, 9, 0, 0)
// 		);
// 		given(stockService.increaseStock(orderItemId))
// 				.willReturn(stockDto);
//
// 		// when
// 		// then
// 		mockMvc.perform(put("/api/internal/stocks/v1/1/increase"))
// 				.andDo(print())
// 				.andExpect(status().isOk())
// 				.andExpect(jsonPath("$.result.id").value(stockDto.getId()))
// 				.andExpect(jsonPath("$.result.productId").value(stockDto.getProductId()))
// 				.andExpect(jsonPath("$.result.total").value(stockDto.getTotal()));
// 	}
//
// }
