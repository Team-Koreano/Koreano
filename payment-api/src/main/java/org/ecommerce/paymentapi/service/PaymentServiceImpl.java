package org.ecommerce.paymentapi.service;

import org.ecommerce.paymentapi.dto.PaymentDto;
import org.ecommerce.paymentapi.entity.Payment;
import org.ecommerce.paymentapi.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl {
	private final PaymentRepository paymentRepository;

	public void createPayment() {
		Payment payment = Payment.builder()
			.orderId(1L)
			.totalAmount(10)
			.orderName("아라비아 커피 외 2개")
			.isDeleted(false)
			.build();
		paymentRepository.save(payment);
	}
}
