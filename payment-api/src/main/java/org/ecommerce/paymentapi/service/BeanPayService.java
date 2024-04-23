
package org.ecommerce.paymentapi.service;

import java.util.UUID;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.client.TossServiceClient;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.dto.BeanPayMapper;
import org.ecommerce.paymentapi.dto.TossDto;
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

	/**
	 * 충전하기 전 사전 객체 생성
	 * @author 이우진

	 * @return - BeanPayDto
	 */
	public BeanPayDto preChargeBeanPay(final BeanPayDto.Request.PreCharge request) {

		final BeanPay beanPay = BeanPay.ofCreate(request.userId(), request.amount());
		final BeanPay createBeanPay = beanPayRepository.save(beanPay);
		return BeanPayMapper.INSTANCE.toDto(createBeanPay);

	}

	/**
	 * 토스에서 전달한 검증객체 검증 메소드
	 * @author 이우진

	 * @param - BeanPayDto.Request.TossPayment request
	 * @return - BeanPayDto response
	 */
	public BeanPayDto validTossCharge(final TossDto.Request.TossPayment request) {
		log.info("request : {} {} {}", request.paymentKey(), request.orderId(),
			request.amount());

		BeanPay beanPay = findBeanPayById(request.orderId());

		try {
			validateBeanPay(beanPay, request);
		} catch (CustomException e) {
			handleException(beanPay, e.getErrorMessage());
			return BeanPayMapper.INSTANCE.toDto(beanPay);
		}
		try {
			//TODO: 유저 beanPay 추가 로직 작성
			processApproval(beanPay, request);
			return BeanPayMapper.INSTANCE.toDto(beanPay);
		} catch (CustomException e) {
			handleException(beanPay, e.getErrorMessage());
			//TODO: 유저 beanPay 롤백 로직 작성
			return BeanPayMapper.INSTANCE.toDto(beanPay);
		}
	}

	/**
	 * userId를 기반 찾기
	 * @author 이우진

	 * @param - UUID orderId
	 * @return - BeanPay
	 */
	private BeanPay findBeanPayById(final UUID orderId) throws CustomException {
		return beanPayRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(BeanPayErrorCode.NOT_EXIST));
	}

	/**
	 * 토스 객체와 DB 값 비교
	 * @author 이우진

	 * @param - BeanPay beanPay, BeanPayDto.Request.TossPayment request
	 * @return - void
	 */
	private void validateBeanPay(final BeanPay beanPay,
		final TossDto.Request.TossPayment request) throws CustomException {
		if (!beanPay.validBeanPay(request.orderId(), request.amount())) {
			throw new CustomException(BeanPayErrorCode.VERIFICATION_FAIL);
		}
	}

	/**
	 * 토스 외부 API 검증 승인 호출
	 * @author 이우진

	 * @param - final BeanPay beanPay, final BeanPayDto.Request.TossPayment request
	 * @return - void
	 */
	private void processApproval(final BeanPay beanPay,
		final TossDto.Request.TossPayment request) throws CustomException {
		ResponseEntity<TossDto.Response.TossPayment> response = tossServiceClient.approvePayment(
			tossKey.getAuthorizationKey(), request);

		if (!response.getStatusCode().is2xxSuccessful()) {
			log.error("토스 결제 승인 실패");
			throw new CustomException(BeanPayErrorCode.TOSS_RESPONSE_FAIL);
		}

		beanPay.chargeComplete(response.getBody());
		log.info("토스 결제 승인 서비스 로직 종료");

	}

	/**
	 * 예외 발생 시 상태 빈페이 상태 변경
	 * @author 이우진

	 * @param - BeanPay beanPay, CustomException e
	 * @return - void
	 */
	private void handleException(final BeanPay beanPay, final String message) {
		beanPay.chargeFail(message);
	}

	/**
	 * 사전결제 실패 시
	 * @author 이우진

	 * @param - BeanPayDto.Request.TossFail request
	 * @return - BeanPayDto
	 */
	public BeanPayDto failTossCharge(BeanPayDto.Request.TossFail request) {

		BeanPay findBeanPay = findBeanPayById(request.orderId());
		handleException(findBeanPay, request.errorMessage());

		return BeanPayMapper.INSTANCE.toDto(findBeanPay);
	}
}