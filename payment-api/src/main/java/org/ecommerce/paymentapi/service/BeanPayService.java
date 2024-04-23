
package org.ecommerce.paymentapi.service;

import java.util.UUID;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.client.TossServiceClient;
import org.ecommerce.paymentapi.dto.BeanPayDto;
import org.ecommerce.paymentapi.dto.BeanPayMapper;
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
	 *
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
	 *
	 * @param - BeanPayDto.Request.TossPayment request
	 * @return - BeanPayDto response
	 */

	@Transactional(readOnly = true)
	public BeanPayDto validTossCharge(final BeanPayDto.Request.TossPayment request) {
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
			//TODO: 유저 beanPay 추가 로직 작성
			processApproval(findBeanPay, request);
			return BeanPayMapper.INSTANCE.toDto(findBeanPay);
		} catch (CustomException e) {
			handleException(findBeanPay, e);
			//TODO: 유저 beanPay 롤백 로직 작성
			throw e;
		}
	}

	/**
	 *	userId를 기반 찾기
	 * @author 이우진
	 *
	 * @param - UUID orderId
	 * @return - BeanPay
	 */
	private BeanPay findBeanPayById(final UUID orderId) throws CustomException {
		return beanPayRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(BeanPayErrorCode.NOT_EXIST));
	}

	/**
	 *	빈페이 검증 진행중으로 상태 변경
	 * @author 이우진
	 *
	 * @param - BeanPay findBeanPay
	 * @return - void
	 */
	private void processInProgress(final BeanPay findBeanPay) {
		findBeanPay.inProgress();
	}

	/**
	 *	토스 객체와 DB 값 비교
	 * @author 이우진
	 *
	 * @param - BeanPay findBeanPay, BeanPayDto.Request.TossPayment request
	 * @return - void
	 */
	private void validateBeanPay(final BeanPay findBeanPay, final BeanPayDto.Request.TossPayment request) throws CustomException {
		if (!findBeanPay.validBeanPay(request.orderId(), request.amount())) {
			throw new CustomException(BeanPayErrorCode.VERIFICATION_FAIL);
		}
	}

	/**
	 * 토스 외부 API 검증 승인 호출
	 * @author 이우진
	 *
	 * @param - final BeanPay findBeanPay, final BeanPayDto.Request.TossPayment request
	 * @return - void
	 */
	private void processApproval(final BeanPay findBeanPay, final BeanPayDto.Request.TossPayment request) throws CustomException {
		ResponseEntity<BeanPayDto.Response.TossPayment> response = tossServiceClient.approvePayment(tossKey.getAuthorizationKey(), request);

		if (!response.getStatusCode().is2xxSuccessful()) {
			log.error("토스 결제 승인 실패");
			throw new CustomException(BeanPayErrorCode.TOSS_RESPONSE_FAIL);
		}

		findBeanPay.complete(response.getBody());
		log.info("토스 결제 승인 서비스 로직 종료");

	}

	/**
	 * 예외 발생 시 상태 빈페이 상태 변경
	 * @author 이우진
	 *
	 * @param - BeanPay findBeanPay, CustomException e
	 * @return - void
	 * */
	private void handleException(final BeanPay findBeanPay, final CustomException e) {
		findBeanPay.fail(e.getErrorCode());
	}
}