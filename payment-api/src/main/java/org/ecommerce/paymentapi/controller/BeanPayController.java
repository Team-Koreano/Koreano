package org.ecommerce.paymentapi.controller;

import static org.ecommerce.paymentapi.entity.type.ProcessStatus.*;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.aop.TimeCheck;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.dto.TossDto;
import org.ecommerce.paymentapi.service.BeanPayService;
import org.ecommerce.paymentapi.service.LockTestService;
import org.ecommerce.paymentapi.service.PaymentServiceImpl;
import org.springframework.http.HttpStatus;
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
	private final LockTestService lockTestService;

	@PostMapping
	public Response<BeanPayDto> preCharge(@RequestBody final BeanPayDto.Request.PreCharge request) {
		final BeanPayDto response = beanPayService.preChargeBeanPay(request);
		return new Response<>(HttpStatus.OK.value(), response);
	}

	@GetMapping("/success")
	public Response<BeanPayDto> validCharge(@Valid final TossDto.Request.TossPayment request) {
		final BeanPayDto response = beanPayService.validTossCharge(request);
		if(response.getProcessStatus() == FAILED) return new Response<>(HttpStatus.BAD_REQUEST.value(), response);
		return new Response<>(HttpStatus.OK.value(), response);
	}

	@GetMapping("/fail")
	public Response<BeanPayDto> failCharge(@Valid final BeanPayDto.Request.TossFail request) {
		final BeanPayDto response = beanPayService.failTossCharge(request);
		return new Response<>(HttpStatus.OK.value(), response);
	}

	@TimeCheck
	@PostMapping("/test2")
	public Response<Void> test2() {
		lockTestService.notUseAopTest("beanPay", 1);
		return new Response<>(HttpStatus.OK.value(), null);
	}


}