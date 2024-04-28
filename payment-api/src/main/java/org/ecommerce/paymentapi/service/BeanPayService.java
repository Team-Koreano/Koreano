
package org.ecommerce.paymentapi.service;

import java.util.UUID;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.client.TossServiceClient;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.dto.BeanPayMapper;
import org.ecommerce.paymentapi.dto.TossDto;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.BeanPayDetail;
import org.ecommerce.paymentapi.entity.type.Role;
import org.ecommerce.paymentapi.exception.BeanPayDetailErrorCode;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.ecommerce.paymentapi.repository.BeanPayDetailRepository;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.ecommerce.paymentapi.utils.TossKey;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BeanPayService {

	private final TossKey tossKey;
	private final BeanPayRepository beanPayRepository;
	private final BeanPayDetailRepository beanPayDetailRepository;
	private final TossServiceClient tossServiceClient;

	/**
	 충전하기 전 사전 객체 생성
	 @return - BeanPayDto
	 */
	public BeanPayDto preChargeBeanPay(final BeanPayDto.Request.PreCharge request) {
		final BeanPay beanPay = getBeanPay(request.userId(), Role.USER);

		final BeanPayDetail beanPayDetail = BeanPayDetail.ofCreate(
			beanPay,
			request.userId(),
			request.amount()
		);
		final BeanPayDetail createBeanPayDetail = beanPayDetailRepository.save(
			beanPayDetail
		);
		return BeanPayMapper.INSTANCE.toDto(createBeanPayDetail);

	}

	private BeanPay getBeanPay(final Integer userId, final Role role) {
		return beanPayRepository.findBeanPayByUserIdAndRole(userId, role)
			.orElseThrow(() -> new CustomException(BeanPayErrorCode.NOT_FOUND_ID));
	}

	/**
	 토스에서 전달한 검증객체 검증 메소드
	 @param - BeanPayDto.Request.TossPayment request
	 @return - BeanPayDto response
	 */
	public BeanPayDto validTossCharge(final TossDto.Request.TossPayment request) {
		log.info("request : {} {} {}", request.paymentKey(), request.orderId(),
			request.amount());
		BeanPayDetail beanPayDetail = getBeanPayDetail(request.orderId());
		try {
			validateBeanPayDetail(beanPayDetail, request);
			processApproval(beanPayDetail, request);
		} catch (CustomException e) {
			handleException(beanPayDetail, e.getErrorMessage());
		}
		return BeanPayMapper.INSTANCE.toDto(beanPayDetail);
	}

	/**
	 userId를 기반 찾기
	 @param - UUID orderId
	 @return - BeanPay
	 */
	private BeanPayDetail getBeanPayDetail(final UUID orderId) throws CustomException {
		return beanPayDetailRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(BeanPayDetailErrorCode.NOT_EXIST));
	}

	/**
	 토스 객체와 DB 값 비교
	 @param - BeanPay beanPay, BeanPayDto.Request.TossPayment request
	 @return - void
	 */
	private void validateBeanPayDetail(final BeanPayDetail beanPayDetail,
		final TossDto.Request.TossPayment request) throws CustomException {
		if (!beanPayDetail.validBeanPay(request.orderId(), request.amount())) {
			throw new CustomException(BeanPayDetailErrorCode.VERIFICATION_FAIL);
		}
	}

	/**
	 토스 외부 API 검증 승인 호출
	 @param - final BeanPay beanPay, final BeanPayDto.Request.TossPayment request
	 @return - void
	 */
	private void processApproval(final BeanPayDetail beanPayDetail,
		final TossDto.Request.TossPayment request) throws CustomException {
		ResponseEntity<TossDto.Response.TossPayment> response = tossServiceClient.approvePayment(
			tossKey.getAuthorizationKey(), request);

		if (!response.getStatusCode().is2xxSuccessful()) {
			log.error("토스 결제 승인 실패");
			throw new CustomException(BeanPayDetailErrorCode.TOSS_RESPONSE_FAIL);
		}

		beanPayDetail.chargeComplete(response.getBody());
		log.info("토스 결제 승인 서비스 로직 종료");

	}

	/**
	 예외 발생 시 상태 빈페이 상태 변경
	 @param - BeanPay beanPay, CustomException e
	 @return - void
	 */
	private void handleException(final BeanPayDetail beanPayDetail,
		final String message) {
		beanPayDetail.chargeFail(message);
	}

	/**
	 사전결제 실패 시
	 @param - BeanPayDto.Request.TossFail request
	 @return - BeanPayDto
	 */
	public BeanPayDto failTossCharge(final BeanPayDto.Request.TossFail request) {

		BeanPayDetail findBeanPayDetail = getBeanPayDetail(request.orderId());
		handleException(findBeanPayDetail, request.errorMessage());

		return BeanPayMapper.INSTANCE.toDto(findBeanPayDetail);
	}
}