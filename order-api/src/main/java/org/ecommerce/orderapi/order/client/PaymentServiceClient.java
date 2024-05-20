package org.ecommerce.orderapi.order.client;

import org.ecommerce.common.config.FeignConfig;
import org.ecommerce.orderapi.order.dto.OrderDto;
import org.ecommerce.orderapi.order.dto.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${payment-service.url}", configuration = FeignConfig.class)
public interface PaymentServiceClient {

	@PostMapping
	PaymentDto.Response paymentOrder(@RequestBody final OrderDto orderDto);
}
