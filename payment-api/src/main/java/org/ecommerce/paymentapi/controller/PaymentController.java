package org.ecommerce.paymentapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.service.PaymentServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/payment")
public class PaymentController {
	private final PaymentServiceImpl paymentService;

	@GetMapping
	public Response<String> testApi() {
		paymentService.createPayment();
		return new Response<>(200, null);
	}
}
