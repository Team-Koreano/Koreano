package org.ecommerce.paymentapi.internal.service;

import org.ecommerce.paymentapi.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
	private final PaymentRepository paymentRepository;

}
