package org.ecommerce.paymentapi.client;

import org.ecommerce.paymentapi.dto.request.TossPaymentRequest;
import org.ecommerce.paymentapi.dto.response.TossPaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "toss", url = "${toss.server.url}")
public interface TossServiceClient {

	@PostMapping("/confirm")
	ResponseEntity<TossPaymentResponse> approvePayment(
		@RequestHeader("Authorization") String authorizationKey, @RequestBody TossPaymentRequest request);

}
