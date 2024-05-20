package org.ecommerce.paymentapi.internal.controller;

import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDto;
import org.ecommerce.paymentapi.dto.PaymentDto.Request.PaymentPrice;
import org.ecommerce.paymentapi.dto.PaymentMapper;
import org.ecommerce.paymentapi.internal.service.PaymentService;
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
	public PaymentDto.Response paymentPrice(@RequestBody PaymentPrice paymentRequest) {
		return PaymentMapper.INSTANCE.paymentDtoToResponse(
			paymentService.paymentPrice(paymentRequest)
		);
	}

	@PostMapping("/cancel")
	public PaymentDetailDto.Response paymentDetailCancel(
		@RequestBody PaymentDetailDto.Request.PaymentCancel paymentCancel) {
		return PaymentMapper.INSTANCE.paymentDetailDtoToResponse(
			paymentService.cancelPaymentDetail(paymentCancel)
		);
	}

}
