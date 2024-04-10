package org.ecommerce.paymentapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.dto.PaymentDto;
import org.ecommerce.paymentapi.service.BeanPayService;
import org.ecommerce.paymentapi.service.PaymentServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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
	private final BeanPayService beanPayService;

	@GetMapping("/beanpay/pre-charge")
	public Response<BeanPayDto.Response> preCharge(@RequestBody final BeanPayDto.Request.PreCharge request) {
		final BeanPayDto.Response preCharge = beanPayService.preChargeBeanPay(request);
		return new Response<>(200, preCharge);
	}


}
