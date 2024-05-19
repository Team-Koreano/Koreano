package org.ecommerce.paymentapi.internal.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.dto.BeanPayDto.Request.CreateBeanPay;
import org.ecommerce.paymentapi.dto.BeanPayMapper;
import org.ecommerce.paymentapi.internal.service.BeanPayService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/internal/beanpay/v1")
public class BeanPayController {

	private final BeanPayService beanPayService;

	@PostMapping
	public Response<BeanPayDto.Response> createBeanPay(@RequestBody final CreateBeanPay createBeanPay) {
		final BeanPayDto response = beanPayService.createBeanPay(createBeanPay);
		return new Response<>(
			HttpStatus.OK.value(),
			BeanPayMapper.INSTANCE.dtoToResponse(response)
		);
	}

}