package org.ecommerce.paymentapi.service;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.client.TossServiceClient;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.ecommerce.paymentapi.utils.TossKey;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
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

	public BeanPayDto.Response.TossPayment validTossCharge(BeanPayDto.Request.TossPayment request) {
		log.info("request : {} {} {}", request.paymentKey(), request.orderId(), request.amount());

		//빈페이 찾기
		final BeanPay findBeanPay = beanPayRepository.findById(request.orderId())
			.orElseThrow(() -> new CustomException(BeanPayErrorCode.NOT_EXIST));
		try{
			// beanPay 진행중 상태 변경
			findBeanPay.inProgress();

			// 빈페이 값 검증하기
			if (!findBeanPay.validBeanPay(request.orderId(), request.amount())) {
				throw new CustomException(BeanPayErrorCode.VERIFICATION_FAIL);
			}
		}catch (CustomException e) {
			findBeanPay.fail(e.getErrorCode());
			throw e;
		}

		try{
			//TODO: 유저에게 beanPay 전달

			//결제 승인
			final ResponseEntity<BeanPayDto.Response.TossPayment> response = tossServiceClient.approvePayment(tossKey.getAuthorizationKey(),  request);

			// 승인 예외
			if(!response.getStatusCode().is2xxSuccessful()){
				log.error("토스 결제 승인 실패");
				throw new CustomException(BeanPayErrorCode.TOSS_RESPONSE_FAIL);
			}

			//충전객체 상태변경
			findBeanPay.complete(response.getBody());
			log.info("토스 결제 승인 서비스 로직 종료");
			return response.getBody();

		}catch(CustomException e) {

			findBeanPay.fail(e.getErrorCode());
			//TODO: 유저에게 beanPay 롤백
			throw e;
		}
	}

}
