package org.ecommerce.orderapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.ecommerce.orderapi.dto.OrderDto;
import org.ecommerce.orderapi.entity.Order;
import org.ecommerce.orderapi.repository.OrderDetailRepository;
import org.ecommerce.orderapi.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

	@InjectMocks
	private OrderService orderService;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderDetailRepository orderDetailRepository;

	@Test
	void 주문_생성() {
		// given
		final Integer USER_ID = 1;
		final OrderDto.Request.Place placeRequest = new OrderDto.Request.Place(
				List.of(1L, 2L, 3L),
				"receiveName",
				"010-777-7777",
				"동백",
				"백동",
				"빠른 배송 부탁드려요"
		);
		final ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
		// when(orderService.)
		// when
		orderService.placeOrder(USER_ID, placeRequest);

		// then
		verify(orderRepository, times(1)).save(orderCaptor.capture());
		assertEquals(placeRequest.receiveName(), orderCaptor.getValue().getReceiveName());
		assertEquals(placeRequest.phoneNumber(), orderCaptor.getValue().getPhoneNumber());
		assertEquals(placeRequest.address1(), orderCaptor.getValue().getAddress1());
		assertEquals(placeRequest.address2(), orderCaptor.getValue().getAddress2());
		assertEquals(placeRequest.deliveryComment(), orderCaptor.getValue().getDeliveryComment());
	}

	@Test
	void 상세주문_생성() {
		// given

		// when

		// then
	}

}
