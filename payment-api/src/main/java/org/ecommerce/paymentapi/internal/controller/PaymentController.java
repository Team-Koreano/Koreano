package org.ecommerce.paymentapi.internal.controller;

import org.ecommerce.paymentapi.external.service.BeanPayService;
import org.ecommerce.paymentapi.internal.service.PaymentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/payment")
public class PaymentController {
	private final PaymentService paymentService;
	private final BeanPayService beanPayService;

}
