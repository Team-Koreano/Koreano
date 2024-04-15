package org.ecommerce.paymentapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.service.BeanPayService;
import org.ecommerce.paymentapi.service.PaymentServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/beanpay/v1")
public class BeanPayController {
	private final PaymentServiceImpl paymentService;
	private final BeanPayService beanPayService;

	@PostMapping("/payments")
	public Response<BeanPayDto.Response> preCharge(@RequestBody final BeanPayDto.Request.PreCharge request) {
		final BeanPayDto.Response preCharge = beanPayService.preChargeBeanPay(request);
		return new Response<>(200, preCharge);
	}


}
