
package org.ecommerce.paymentapi.external.service;

import static org.ecommerce.paymentapi.entity.enumerate.LockName.*;

import java.util.UUID;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.aop.DistributedLock;
import org.ecommerce.paymentapi.client.TossServiceClient;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PreCharge;
import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.TossFail;
import org.ecommerce.paymentapi.dto.PaymentDetailMapper;
import org.ecommerce.paymentapi.dto.TossDto;
import org.ecommerce.paymentapi.dto.TossDto.Request.TossPayment;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.PaymentDetail;
import org.ecommerce.paymentapi.entity.enumerate.Role;
import org.ecommerce.paymentapi.exception.BeanPayErrorCode;
import org.ecommerce.paymentapi.exception.PaymentDetailErrorCode;
import org.ecommerce.paymentapi.repository.BeanPayRepository;
import org.ecommerce.paymentapi.repository.PaymentDetailRepository;
import org.ecommerce.paymentapi.utils.TossKey;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BeanPayService {

	private final TossKey tossKey;
	private final BeanPayRepository beanPayRepository;
	private final TossServiceClient tossServiceClient;
	private final PaymentDetailRepository paymentDetailRepository;

	/**
	 충전하기 전 사전 객체 생성
	 @return - BeanPayDto
	 */
	@Transactional
	public PaymentDetailDto beforeCharge(final PreCharge request) {

		final BeanPay beanPay = getBeanPay(request.userId(), Role.USER);
		final PaymentDetail paymentDetail = beanPay.beforeCharge(request.chargeAmount());

		return PaymentDetailMapper.INSTANCE.entityToDto(
			paymentDetailRepository.save(paymentDetail)
		);

	}

	private BeanPay getBeanPay(final Integer userId, final Role role) {
		final BeanPay beanPay = beanPayRepository.findBeanPayByUserIdAndRole(
			userId, role);
		if(beanPay == null)
			new CustomException(BeanPayErrorCode.NOT_FOUND_ID);
		return beanPay;
	}

	/**
	 토스에서 전달한 검증객체 검증 메소드
	 @param - TossPayment request
	 @return - BeanPayDto response
	 */
	@DistributedLock(
		lockName = BEANPAY,
		uniqueKey = "#userId + #role.name()")
	public PaymentDetailDto validTossCharge(
		final TossPayment request,
		final Integer userId,
		final Role role
	) {
		log.info("request : {} {} {}", request.paymentKey(), request.orderId(),
			request.chargeAmount());
		PaymentDetail paymentDetail = getPaymentDetail(request.orderId());
		try {
			validateBeanPayDetail(paymentDetail, request);
			processApproval(paymentDetail, request);
		} catch (CustomException e) {
			handleException(paymentDetail, e.getErrorMessage());
		}
		return PaymentDetailMapper.INSTANCE.entityToDto(paymentDetail);
	}

	/**
	 userId를 기반 찾기
	 @param - UUID orderId
	 @return - BeanPay
	 */
	private PaymentDetail getPaymentDetail(final UUID id) throws CustomException {
		PaymentDetail paymentDetail = paymentDetailRepository.findPaymentDetailById(id);
		if(paymentDetail == null)
			throw new CustomException(PaymentDetailErrorCode.NOT_FOUND_ID);
		return paymentDetail;
	}


	private PaymentDetail getPaymentDetail(final Long orderItemId) throws CustomException {
		PaymentDetail paymentDetail = paymentDetailRepository.findPaymentDetailByOrderItemId(
			orderItemId);
		if(paymentDetail == null)
			throw new CustomException(PaymentDetailErrorCode.NOT_FOUND_ID);
		return paymentDetail;
	}

	/**
	 토스 객체와 DB 값 비교
	 @param - BeanPay beanPay, TossPayment request
	 @return - void
	 */
	private void validateBeanPayDetail(final PaymentDetail paymentDetail,
		final TossPayment request) throws CustomException {
		if (!paymentDetail.validCharge(request.orderId(), request.chargeAmount())) {
			throw new CustomException(PaymentDetailErrorCode.VERIFICATION_FAIL);
		}
	}

	/**
	 토스 외부 API 검증 승인 호출
	 @param - BeanPay beanPay, TossPayment request
	 @return - void
	 */
	private void processApproval(final PaymentDetail paymentDetail,
		final TossPayment request) throws CustomException {
		final ResponseEntity<TossDto.Response.TossPayment> response =
			tossServiceClient.approvePayment(
				tossKey.getAuthorizationKey(), request);

		if (!response.getStatusCode().is2xxSuccessful()) {
			log.error("토스 결제 승인 실패");
			throw new CustomException(PaymentDetailErrorCode.TOSS_RESPONSE_FAIL);
		}

		paymentDetail.chargeComplete(response.getBody());
		log.info("토스 결제 승인 서비스 로직 종료");

	}

	/**
	 예외 발생 시 상태 빈페이 상태 변경
	 @param - BeanPay beanPay, String message
	 @return - void
	 */
	private void handleException(final PaymentDetail paymentDetail,
		final String message) {
		paymentDetail.chargeFail(message);
	}

	/**
	 사전결제 실패 시
	 @param - BeanPayDto.Request.TossFail request
	 @return - BeanPayDto
	 */
	@Transactional
	public PaymentDetailDto failTossCharge(final TossFail request) {

		final PaymentDetail paymentDetail = getPaymentDetail(request.orderId());
		handleException(paymentDetail, request.errorMessage());

		return PaymentDetailMapper.INSTANCE.entityToDto(paymentDetail);
	}


}