package org.ecommerce.paymentapi.service;

import java.util.UUID;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.client.TossServiceClient;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.ecommerce.paymentapi.utils.TossKey;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BeanPayService {

	private final TossKey tossKey;
	private final BeanPayRepository beanPayRepository;
	private final TossServiceClient tossServiceClient;

	public BeanPayDto.Response preChargeBeanPay(final BeanPayDto.Request.PreCharge request) {

		final BeanPay beanPay = BeanPay.ofCreate(request.userId(), request.amount());
		final BeanPay createBeanPay = beanPayRepository.save(beanPay);

		return BeanPayDto.Response.ofCreate(createBeanPay);
	}
	@Transactional(readOnly = true)
	public BeanPayDto.Response.TossPayment validTossCharge(BeanPayDto.Request.TossPayment request) {
		log.info("request : {} {} {}", request.paymentKey(), request.orderId(), request.amount());

		BeanPay findBeanPay = findBeanPayById(request.orderId());

		try {
			processInProgress(findBeanPay);
			validateBeanPay(findBeanPay, request);
		} catch (CustomException e) {
			handleException(findBeanPay, e);
			throw e;
		}
		try {
			return processApproval(findBeanPay, request);
		} catch (CustomException e) {
			handleException(findBeanPay, e);
			throw e;
		}
	}

	private BeanPay findBeanPayById(UUID orderId) throws CustomException {
		return beanPayRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(BeanPayErrorCode.NOT_EXIST));
	}

	private void processInProgress(BeanPay findBeanPay) {
		findBeanPay.inProgress();
	}

	private void validateBeanPay(BeanPay findBeanPay, BeanPayDto.Request.TossPayment request) throws CustomException {
		if (!findBeanPay.validBeanPay(request.orderId(), request.amount())) {
			throw new CustomException(BeanPayErrorCode.VERIFICATION_FAIL);
		}
	}

	private BeanPayDto.Response.TossPayment processApproval(BeanPay findBeanPay, BeanPayDto.Request.TossPayment request) throws CustomException {
		ResponseEntity<BeanPayDto.Response.TossPayment> response = tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request);

		if (!response.getStatusCode().is2xxSuccessful()) {
			log.error("토스 결제 승인 실패");
			throw new CustomException(BeanPayErrorCode.TOSS_RESPONSE_FAIL);
		}

		findBeanPay.complete(response.getBody());
		log.info("토스 결제 승인 서비스 로직 종료");
		return response.getBody();
	}

	private void handleException(BeanPay findBeanPay, CustomException e) {
		findBeanPay.fail(e.getErrorCode());
	}
}
