package org.ecommerce.paymentapi.internal.controller;

import org.ecommerce.paymentapi.dto.request.DeleteSellerBeanPayRequest;
import org.ecommerce.paymentapi.dto.request.DeleteUserBeanPayRequest;
import org.ecommerce.paymentapi.dto.request.CreateSellerBeanPayRequest;
import org.ecommerce.paymentapi.dto.request.CreateUserBeanPayRequest;
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

	@PostMapping("/user")
	public void createUserBeanPay(@RequestBody final CreateUserBeanPayRequest createUserBeanPay) {
		beanPayService.createUserBeanPay(createUserBeanPay);
	}

	@DeleteMapping("/user")
	public void deleteUserBeanPay(@RequestBody final DeleteUserBeanPayRequest deleteUserBeanPayRequest) {
		beanPayService.deleteUserBeanPay(deleteUserBeanPayRequest);
	}

	@PostMapping("/seller")
	public void createSellerBeanPay(@RequestBody final CreateSellerBeanPayRequest createSellerBeanPay) {
		beanPayService.createSellerBeanPay(createSellerBeanPay);
	}

	@DeleteMapping("/seller")
	public void deleteSellerBeanPay(@RequestBody final DeleteSellerBeanPayRequest deleteSellerBeanPayRequest) {
		beanPayService.deleteSellerBeanPay(deleteSellerBeanPayRequest);
	}



}