package org.ecommerce.orderapi.order.external.controller;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.order.dto.OrderMapper;
import org.ecommerce.orderapi.order.dto.response.InquiryOrderItemResponse;
import org.ecommerce.orderapi.order.dto.response.InquiryOrderItemStatusHistoryResponse;
import org.ecommerce.orderapi.order.service.OrderItemReadService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external/orderItems/v1")
public class OrderItemController {

	private final OrderItemReadService orderItemReadService;

	private final static Integer SELLER_ID = 1;

	@GetMapping
	public Response<List<InquiryOrderItemResponse>> getOrderItems(
			@RequestParam(required = false) final Integer month,
			@RequestParam(required = false, defaultValue = "0") final Integer pageNumber,
			@RequestParam(required = false, defaultValue = "10") final Integer pageSize
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				orderItemReadService.getOrderItems(
								SELLER_ID, month, PageRequest.of(pageNumber, pageSize))
						.stream()
						.map(OrderMapper.INSTANCE::toInquiryOrderItemResponse)
						.toList()
		);
	}

	@GetMapping("/{orderItemId}/statusHistories")
	public Response<InquiryOrderItemStatusHistoryResponse> getOrderItemStatusHistories(
			@PathVariable("orderItemId") final Long orderItemId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				OrderMapper.INSTANCE.toInquiryOrderItemStatusHistoryResponse(
						orderItemReadService.getOrderItemStatusHistories(orderItemId)
				)
		);
	}
}
