package org.ecommerce.paymentapi.internal.controller;

import org.ecommerce.paymentapi.dto.DeleteBeanPayRequest;
import org.ecommerce.paymentapi.dto.request.CreateBeanPayRequest;
import org.ecommerce.paymentapi.internal.service.BeanPayService;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	public void createBeanPay(@RequestBody final CreateBeanPayRequest createBeanPay) {
		beanPayService.createBeanPay(createBeanPay);
	}

	@DeleteMapping
	public void deleteBeanPay(@RequestBody final DeleteBeanPayRequest deleteBeanPay) {
		beanPayService.deleteBeanPay(deleteBeanPay);
	}

}