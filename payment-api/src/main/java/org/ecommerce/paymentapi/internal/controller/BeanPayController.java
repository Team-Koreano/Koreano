package org.ecommerce.paymentapi.internal.controller;

import org.ecommerce.paymentapi.dto.BeanPayMapper;
import org.ecommerce.paymentapi.dto.response.BeanPayResponse;
import org.ecommerce.paymentapi.dto.request.CreateBeanPayRequest;
import org.ecommerce.paymentapi.internal.service.BeanPayService;
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
	public BeanPayResponse createBeanPay(@RequestBody final CreateBeanPayRequest createBeanPay) {
		return BeanPayMapper.INSTANCE.toResponse(
			beanPayService.createBeanPay(createBeanPay)
		);
	}

}