package org.ecommerce.paymentapi.service;

import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BeanPayService {

	private final BeanPayRepository beanPayRepository;

	@Transactional
	public BeanPayDto.Response preChargeBeanPay(final BeanPayDto.Request.PreCharge request) {
		// TODO: 유저 금액있는지 검증로직 구현예정
		
		final BeanPay beanPay = BeanPay.ofCreate(request.userId(), request.amount());
		final BeanPay createBeanPay = beanPayRepository.save(beanPay);

		return BeanPayDto.Response.ofCreate(createBeanPay);
	}



}
