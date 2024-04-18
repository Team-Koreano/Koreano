package org.ecommerce.paymentapi.controller;

import static org.ecommerce.paymentapi.entity.type.ProcessStatus.*;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.service.BeanPayService;
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

	@PostMapping("/payments")
	public Response<BeanPayDto> preCharge(@RequestBody final BeanPayDto.Request.PreCharge request) {
		final BeanPayDto response = beanPayService.preChargeBeanPay(request);
		return new Response<>(HttpStatus.OK.value(), response);
	}

	@GetMapping("/success")
	public Response<BeanPayDto> validCharge(@Valid final BeanPayDto.Request.TossPayment request) {
		final BeanPayDto response = beanPayService.validTossCharge(request);
		if(response.getProcessStatus() == CANCELLED) return new Response<>(HttpStatus.BAD_REQUEST.value(), response);
		return new Response<>(HttpStatus.OK.value(), response);
	}
		return new Response<>(HttpStatus.OK.value(), response);
	}


}