package org.ecommerce.paymentapi.external.controller;

import java.time.LocalDateTime;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentMapper;
import org.ecommerce.paymentapi.dto.response.PaymentDetailResponse;
import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.external.service.PaymentDetailReadService;
import org.ecommerce.paymentapi.utils.PaymentTimeFormatUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
		@RequestParam String startDateTime,
		@RequestParam String endDateTime,
		@RequestParam(required = false) PaymentStatus status,
		Pageable pageable
	) {
		LocalDateTime start = PaymentTimeFormatUtil.stringToDateTime(startDateTime);
		LocalDateTime end = PaymentTimeFormatUtil.stringToDateTime(endDateTime);
		//TODO: jwt userId 주입 예정
		Page<PaymentDetailDto> paymentDetailDtoPage =
			paymentDetailReadService.getUserPaymentDetailsByBetweenDate(
				999, start, end, status, pageable
			);
		return new Response<>(
			HttpStatus.OK.value(),
			new PageImpl<>(
				paymentDetailDtoPage.getContent().stream()
					.map(PaymentMapper.INSTANCE::toPaymentDetailResponse)
					.toList(),
				pageable,
				paymentDetailDtoPage.getTotalPages())
		);
	}

	@GetMapping("/seller")
	public Response<Page<PaymentDetailResponse>> getSellerPayments(
		@RequestParam String startDateTime,
		@RequestParam String endDateTime,
		@RequestParam(required = false) PaymentStatus status,
		Pageable pageable
	) {
		LocalDateTime start = PaymentTimeFormatUtil.stringToDateTime(startDateTime);
		LocalDateTime end = PaymentTimeFormatUtil.stringToDateTime(endDateTime);
		//TODO: jwt sellerId 주입 예정
		Page<PaymentDetailDto> paymentDetailDtoPage =
			paymentDetailReadService.getSellerPaymentDetailByBetweenRange(
				1000, start, end, status, pageable
			);
		return new Response<>(
			HttpStatus.OK.value(),
			new PageImpl<>(
				paymentDetailDtoPage.getContent().stream()
					.map(PaymentMapper.INSTANCE::toPaymentDetailResponse)
					.toList(),
				pageable,
				paymentDetailDtoPage.getTotalPages())
		);
	}
}
