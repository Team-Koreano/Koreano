package org.ecommerce.orderapi.order.controller.external;

import static org.ecommerce.orderapi.order.entity.enumerated.OrderStatus.*;
import static org.ecommerce.orderapi.order.exception.ErrorMessage.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.orderapi.ControllerTest;
import org.ecommerce.orderapi.order.dto.OrderDtoWithOrderItemDtoList;
import org.ecommerce.orderapi.order.dto.OrderItemDto;
import org.ecommerce.orderapi.order.dto.request.CreateOrderRequest;
import org.ecommerce.orderapi.order.entity.enumerated.OrderStatusReason;
import org.ecommerce.orderapi.order.external.controller.OrderController;
import org.ecommerce.orderapi.order.service.OrderDomainService;
import org.ecommerce.orderapi.order.service.OrderReadService;
import org.ecommerce.orderapi.stock.service.StockDomainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrderController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest extends ControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private OrderDomainService orderDomainService;

	@MockBean
	private OrderReadService orderReadService;

	@MockBean
	private StockDomainService stockDomainService;

	@Test
	void 주문하기() throws Exception {
		// given
		OrderDtoWithOrderItemDtoList orderDto = new OrderDtoWithOrderItemDtoList(
				1L,
				1,
				"userName",
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드려요.",
				10000,
				APPROVE,
				LocalDateTime.of(2024, 4, 22, 0, 1, 0, 1),
				LocalDateTime.of(2024, 4, 22, 0, 1, 0, 1),
				LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1),
				List.of()
		);
		when(orderDomainService.createOrder(any(), any(CreateOrderRequest.class)))
				.thenReturn(orderDto);

		// when
		// then
		mockMvc.perform(post("/api/external/orders/v1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(
								new CreateOrderRequest(
										List.of(1L, 2L, 3L),
										"receiveName",
										"010-777-7777",
										"동백",
										"백동",
										"빠른 배송 부탁드려요"
								)
						)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result.id").value(orderDto.id()))
				.andExpect(jsonPath("$.result.userId").value(orderDto.userId()))
				.andExpect(
						jsonPath("$.result.receiveName").value(orderDto.receiveName()))
				.andExpect(
						jsonPath("$.result.phoneNumber").value(orderDto.phoneNumber()))
				.andExpect(jsonPath("$.result.address1").value(orderDto.address1()))
				.andExpect(jsonPath("$.result.address2").value(orderDto.address2()))
				.andExpect(jsonPath("$.result.deliveryComment").value(
						orderDto.deliveryComment()))
				.andExpect(jsonPath("$.result.totalPaymentAmount").value(
						orderDto.totalPaymentAmount()))
				.andDo(print());
	}

	@Test
	void 필수정보_없이_주문하기() throws Exception {
		// given
		CreateOrderRequest request = new CreateOrderRequest(
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
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
				.andExpect(jsonPath("$.result").value(ADDRESS1_NOT_BLANK));
	}

	@Test
	void 주문내역_조회() throws Exception {
		// given
		final int pageNumber = 1;
		final int pageSize = 5;
		final long total = 1L;
		Page<OrderDtoWithOrderItemDtoList> expectResult = new PageImpl<>(
				List.of(
						new OrderDtoWithOrderItemDtoList(
								1L,
								1,
								"userName",
								"receiveName",
								"010-777-7777",
								"동백",
								"백동",
								"빠른 배송 부탁드려요.",
								10000,
								APPROVE,
								LocalDateTime.of(2024, 4, 22, 0, 1, 0, 1),
								LocalDateTime.of(2024, 4, 22, 0, 1, 0, 1),
								LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1),
								List.of(
										new OrderItemDto(
												1L,
												101,
												"productName1",
												10000,
												1,
												10000,
												0,
												10000,
												1,
												"seller1",
												OPEN,
												null,
												LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1),
												LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1)
										)
								)
						)
				),
				PageRequest.of(pageNumber, pageSize),
				total
		);
		when(orderReadService.getOrders(any(), any(), any(), any()))
				.thenReturn(expectResult);

		// when
		// then
		OrderDtoWithOrderItemDtoList orderDto = expectResult.getContent().get(0);
		OrderItemDto orderItemDto = orderDto.orderItemDtoList().get(0);
		mockMvc.perform(get("/api/external/orders/v1"))
				.andDo(print())
				.andExpect(jsonPath("$.result.content[0].id").value(orderDto.id()))
				.andExpect(jsonPath("$.result.content[0].userId").value(orderDto.userId()))
				.andExpect(jsonPath("$.result.content[0].receiveName")
						.value(orderDto.receiveName()))
				.andExpect(jsonPath("$.result.content[0].address1")
						.value(orderDto.address1()))
				.andExpect(jsonPath("$.result.content[0].address2")
						.value(orderDto.address2()))
				.andExpect(jsonPath("$.result.content[0].deliveryComment")
						.value(orderDto.deliveryComment()))
				.andExpect(jsonPath("$.result.content[0].orderDatetime")
						.value(orderDto.orderDatetime().toString()))
				.andExpect(jsonPath("$.result.content[0].orderItemResponses[0].status")
						.value(orderItemDto.status().toString()))
				.andExpect(status().isOk());

	}

	@Test
	void 주문_취소() throws Exception {
		// given
		OrderItemDto orderItemDto = new OrderItemDto(
				1L,
				101,
				"productName1",
				10000,
				1,
				10000,
				0,
				10000,
				1,
				"sellerName1",
				CANCELLED,
				OrderStatusReason.REFUND,
				LocalDateTime.of(2024, 5, 8, 0, 0),
				LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1)
		);
		OrderDtoWithOrderItemDtoList orderDto = new OrderDtoWithOrderItemDtoList(
				1L,
				1,
				"userName",
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드려요.",
				10000,
				CANCELLED,
				LocalDateTime.of(2024, 4, 22, 0, 1, 0, 1),
				LocalDateTime.of(2024, 4, 22, 0, 1, 0, 1),
				LocalDateTime.of(2024, 4, 22, 0, 2, 0, 1),
				List.of(orderItemDto)
		);

		given(orderDomainService.cancelOrder(any(), anyLong(), anyLong()))
				.willReturn(orderDto);
		// when
		// then
		mockMvc.perform(delete("/api/external/orders/v1/1/orderItems/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.result.id").value(orderItemDto.id()))
				.andExpect(
						jsonPath("$.result.orderItemResponses[0].productId").value(
								orderItemDto.productId()))
				.andExpect(jsonPath("$.result.orderItemResponses[0].productName").value(
						orderItemDto.productName()))
				.andExpect(jsonPath("$.result.orderItemResponses[0].price").value(
						orderItemDto.price()))
				.andExpect(
						jsonPath("$.result.orderItemResponses[0].quantity").value(
								orderItemDto.quantity()))
				.andExpect(jsonPath("$.result.orderItemResponses[0].totalPrice").value(
						orderItemDto.totalPrice()))
				.andExpect(jsonPath("$.result.orderItemResponses[0].paymentAmount").value(
						orderItemDto.paymentAmount()))
				.andExpect(
						jsonPath("$.result.orderItemResponses[0].sellerId").value(
								orderItemDto.sellerId()))
				.andExpect(jsonPath("$.result.orderItemResponses[0].sellerName").value(
						orderItemDto.sellerName()))
				.andExpect(jsonPath("$.result.orderItemResponses[0].status").value(
						orderItemDto.status().toString()))
				.andExpect(jsonPath("$.result.orderItemResponses[0].statusReason").value(
						orderItemDto.statusReason().toString()))
				.andDo(print());
	}

}
