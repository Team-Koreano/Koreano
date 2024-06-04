package org.ecommerce.paymentapi.internal.controller;

import org.ecommerce.paymentapi.dto.PaymentMapper;
import org.ecommerce.paymentapi.dto.request.PaymentCancelRequest;
import org.ecommerce.paymentapi.dto.request.PaymentPriceRequest;
import org.ecommerce.paymentapi.dto.response.PaymentDetailResponse;
import org.ecommerce.paymentapi.dto.response.PaymentWithDetailResponse;
import org.ecommerce.paymentapi.internal.service.PaymentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/internal/payment/v1")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	public PaymentWithDetailResponse paymentPrice(@RequestBody final PaymentPriceRequest paymentRequest) {
		return PaymentMapper.INSTANCE.toPaymentWithDetailResponse(
			paymentService.paymentPrice(paymentRequest)
		);
	}

	@DeleteMapping
	public PaymentDetailResponse paymentDetailCancel(
		@RequestBody final PaymentCancelRequest paymentCancel) {
		return PaymentMapper.INSTANCE.toPaymentDetailResponse(
			paymentService.cancelPaymentDetail(paymentCancel)
		);
	}

}
