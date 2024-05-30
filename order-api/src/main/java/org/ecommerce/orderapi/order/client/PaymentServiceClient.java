package org.ecommerce.orderapi.order.client;

import org.ecommerce.common.config.FeignConfig;
import org.ecommerce.orderapi.order.dto.OrderDtoWithOrderItemDtoList;
import org.ecommerce.orderapi.order.dto.response.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${payment-service.url}", configuration = FeignConfig.class)
public interface PaymentServiceClient {

	@PostMapping
	PaymentResponse paymentOrder(
			@RequestBody final OrderDtoWithOrderItemDtoList orderDtoWithOrderItemDtoList);
}
