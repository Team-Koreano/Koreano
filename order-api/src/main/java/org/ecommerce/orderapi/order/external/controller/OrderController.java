package org.ecommerce.orderapi.order.external.controller;

import org.ecommerce.common.security.AuthDetails;
import org.ecommerce.common.security.custom.CurrentUser;
import org.ecommerce.common.vo.Response;
import org.ecommerce.orderapi.order.dto.OrderMapper;
import org.ecommerce.orderapi.order.dto.request.CreateOrderRequest;
import org.ecommerce.orderapi.order.dto.response.CreateOrderResponse;
import org.ecommerce.orderapi.order.dto.response.InquiryOrderResponse;
import org.ecommerce.orderapi.order.service.OrderDomainService;
import org.ecommerce.orderapi.order.service.OrderReadService;
import org.ecommerce.orderapi.stock.dto.StockDto;
import org.ecommerce.orderapi.stock.dto.StockMapper;
import org.ecommerce.orderapi.stock.service.StockDomainService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external/orders/v1")
public class OrderController {

	private final OrderReadService orderReadService;
	private final StockDomainService stockDomainService;
	private final OrderDomainService orderDomainService;

	@PostMapping
	public Response<CreateOrderResponse> createOrder(
			@CurrentUser final AuthDetails authDetails,
			@RequestBody @Valid final CreateOrderRequest createRequest
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				OrderMapper.INSTANCE.toCreateOrderResponse(
						orderDomainService.createOrder(authDetails.getId(), createRequest)
				)
		);
	}

	@GetMapping
	public Response<Page<InquiryOrderResponse>> getOrders(
			@CurrentUser final AuthDetails authDetails,
			@RequestParam(name = "year", required = false) final Integer year,
			@RequestParam(name = "pageNumber", required = false, defaultValue = "1") final Integer pageNumber,
			@RequestParam(name = "pageSize", required = false, defaultValue = "5") final Integer pageSize

	) {

		return new Response<>(
				HttpStatus.OK.value(),
				orderReadService.getOrders(authDetails.getId(), year, pageNumber, pageSize)
						.map(OrderMapper.INSTANCE::toInquiryOrderResponse)
		);
	}

	@DeleteMapping("/{orderId}/orderItems/{orderItemId}")
	public Response<InquiryOrderResponse> cancelOrder(
			@CurrentUser final AuthDetails authDetails,
			@PathVariable("orderId") final Long orderId,
			@PathVariable("orderItemId") final Long orderItemId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				OrderMapper.INSTANCE.toInquiryOrderResponse(
						orderDomainService.cancelOrder(authDetails.getId(), orderId, orderItemId)
				)
		);
	}

	@GetMapping("/mocks/{productId}")
	public Response<StockDto.Response> getMock(
			@PathVariable("productId") final Integer productId
	) {

		return new Response<>(
				HttpStatus.OK.value(),
				StockMapper.INSTANCE.toResponse(
						stockDomainService.getMockData(productId)
				)
		);
	}

	@PostMapping("/mocks")
	public void saveMockData() {
		stockDomainService.saveMock();
	}
}
