package org.ecommerce.paymentapi.external.controller;

import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.Role.*;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.aop.TimeCheck;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.dto.PaymentDto;
import org.ecommerce.paymentapi.dto.TossDto;
import org.ecommerce.paymentapi.external.service.BeanPayService;
import org.ecommerce.paymentapi.external.service.LockTestService;
import org.ecommerce.paymentapi.internal.service.PaymentService;
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
@RequestMapping("/api/external/beanpay/v1")
public class BeanPayController {
	private final PaymentService paymentService;
	private final BeanPayService beanPayService;
	private final LockTestService lockTestService;

	@PostMapping
	public Response<BeanPayDto> preCharge(@RequestBody final BeanPayDto.Request.PreCharge request) {
		final BeanPayDto response = beanPayService.preChargeBeanPay(request);
		return new Response<>(HttpStatus.OK.value(), response);
	}

	@GetMapping("/success")
	public Response<BeanPayDto> validCharge(@Valid final TossDto.Request.TossPayment request) {
		//TODO: Id, Role 적용 예정
		final BeanPayDto response = beanPayService.validTossCharge(request, 1, USER);
		if(response.getProcessStatus() == FAILED) return new Response<>(HttpStatus.BAD_REQUEST.value(), response);
		return new Response<>(HttpStatus.OK.value(), response);
	}

	@GetMapping("/fail")
	public Response<BeanPayDto> failCharge(@Valid final BeanPayDto.Request.TossFail request) {
		final BeanPayDto response = beanPayService.failTossCharge(request);
		return new Response<>(HttpStatus.OK.value(), response);
	}

	@TimeCheck
	@PostMapping("/multilock")
	public Response<Void> test1() {
		lockTestService.useMultiLockTest(new PaymentDto.Request.PaymentPrice(
			1L,
			5000,
			1,
			1,
			"orderName",
			List.of()
		));
		return new Response<>(HttpStatus.OK.value(), null);
	}

	@TimeCheck
	@PostMapping("/singlelock")
	public Response<Void> test2() {
		lockTestService.useDistributeLock("beanPay", 1);
		return new Response<>(HttpStatus.OK.value(), null);
	}


}