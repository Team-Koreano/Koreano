package org.ecommerce.paymentapi.external.controller;

import java.time.LocalDateTime;

import org.ecommerce.common.security.AuthDetails;
import org.ecommerce.common.security.custom.CurrentUser;
import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.PaymentMapper;
import org.ecommerce.paymentapi.dto.response.PaymentDetailResponse;
import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.external.service.PaymentDetailReadService;
import org.ecommerce.paymentapi.utils.PaymentTimeFormatUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/external/paymentdetail/v1")
public class PaymentDetailController {
	private final PaymentDetailReadService paymentDetailReadService;

	@GetMapping("/user")
	public Response<Page<PaymentDetailResponse>> getUserPayments(
		@CurrentUser AuthDetails auth,
		@RequestParam String startDateTime,
		@RequestParam String endDateTime,
		@RequestParam(required = false) PaymentStatus status,
		Pageable pageable
	) {
		LocalDateTime start = PaymentTimeFormatUtil.stringToDateTime(startDateTime);
		LocalDateTime end = PaymentTimeFormatUtil.stringToDateTime(endDateTime);

		return new Response<>(
			HttpStatus.OK.value(),
			paymentDetailReadService.getUserPaymentDetailsByBetweenDate(
				auth.getId(), start, end, status, pageable
			).map(PaymentMapper.INSTANCE::toPaymentDetailResponse)
		);
	}

	@GetMapping("/seller")
	public Response<Page<PaymentDetailResponse>> getSellerPayments(
		@CurrentUser AuthDetails auth,
		@RequestParam String startDateTime,
		@RequestParam String endDateTime,
		@RequestParam(required = false) PaymentStatus status,
		Pageable pageable
	) {
		LocalDateTime start = PaymentTimeFormatUtil.stringToDateTime(startDateTime);
		LocalDateTime end = PaymentTimeFormatUtil.stringToDateTime(endDateTime);
		//TODO: jwt sellerId 주입 예정


		return new Response<>(
			HttpStatus.OK.value(),
			paymentDetailReadService.getSellerPaymentDetailByBetweenRange(
				auth.getId(), start, end, status, pageable
			).map(PaymentMapper.INSTANCE::toPaymentDetailResponse)
		);
	}
}
