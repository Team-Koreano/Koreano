package org.ecommerce.paymentapi.client;

import org.ecommerce.paymentapi.dto.TossDto;
import org.ecommerce.paymentapi.dto.TossDto.Response.TossPayment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "toss", url = "https://api.tosspayments.com/v1/payments")
public interface TossServiceClient {

	@PostMapping("/confirm")
	ResponseEntity<TossPayment> approvePayment(
		@RequestHeader("Authorization") String authorizationKey, @RequestBody TossDto.Request.TossPayment request);

}
