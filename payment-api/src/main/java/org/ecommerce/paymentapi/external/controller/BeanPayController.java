package org.ecommerce.paymentapi.external.controller;

import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.Role.*;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PreCharge;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.TossFail;
import org.ecommerce.paymentapi.dto.TossDto.Request.TossPayment;
import org.ecommerce.paymentapi.external.service.BeanPayService;
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

	private final BeanPayService beanPayService;

	@PostMapping("/charge")
	public Response<PaymentDetailDto> beforeCharge(@RequestBody final PreCharge request) {
		final PaymentDetailDto response = beanPayService.preCharge(request);
		return new Response<>(HttpStatus.OK.value(), response);
	}

	@GetMapping("/success")
	public Response<PaymentDetailDto> validCharge(@Valid final TossPayment request) {
		//TODO: Id, Role 적용 예정
		final PaymentDetailDto response = beanPayService.validTossCharge(request, 1, USER);
		if(response.getProcessStatus() == FAILED) return new Response<>(HttpStatus.BAD_REQUEST.value(), response);
		return new Response<>(HttpStatus.OK.value(), response);
	}

	@GetMapping("/fail")
	public Response<PaymentDetailDto> failCharge(@Valid final TossFail request) {
		final PaymentDetailDto response = beanPayService.failTossCharge(request);
		return new Response<>(HttpStatus.OK.value(), response);
	}



}