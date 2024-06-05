package org.ecommerce.paymentapi.external.controller;

import java.util.List;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.aop.TimeCheck;
import org.ecommerce.paymentapi.dto.request.PaymentPriceRequest;
import org.ecommerce.paymentapi.external.service.LockTestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/external/test/v1")
public class TestController {

	private final LockTestService lockTestService;

	@TimeCheck
	@PostMapping("/multilock")
	public Response<Void> test1() {
		lockTestService.useMultiLockTest(new PaymentPriceRequest(
			1L,
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
