package org.ecommerce.paymentapi.external.controller;

import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;

import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDetailMapper;
import org.ecommerce.paymentapi.dto.request.PreChargeRequest;
import org.ecommerce.paymentapi.dto.request.TossFailRequest;
import org.ecommerce.paymentapi.dto.request.TossPaymentRequest;
import org.ecommerce.paymentapi.dto.response.PaymentDetailResponse;
import org.ecommerce.paymentapi.external.service.BeanPayService;
import org.ecommerce.paymentapi.external.service.PaymentDetailReadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/external/beanpay/v1")
public class BeanPayController {

	private final BeanPayService beanPayService;
	private final PaymentDetailReadService paymentDetailReadService;

	@PostMapping("/charge")
	public Response<PaymentDetailResponse> beforeCharge(@RequestBody final PreChargeRequest request) {
		final PaymentDetailDto response = beanPayService.beforeCharge(request);
		return new Response<>(
			HttpStatus.OK.value(),
			PaymentDetailMapper.INSTANCE.toResponse(response)
		);
	}

	@GetMapping("/success")
	public Response<PaymentDetailResponse> validCharge(@Valid final TossPaymentRequest request) {
		//TODO: Jwt 회원 Id 적용 예정
		final PaymentDetailResponse response =
			PaymentDetailMapper.INSTANCE.toResponse(beanPayService.validTossCharge(request, 1));
		if(response.processStatus() == FAILED) {
			return new Response<>(HttpStatus.BAD_REQUEST.value(), response);
		}
		return new Response<>(
			HttpStatus.OK.value(),
			response
		);
	}

	@GetMapping("/fail")
	public Response<PaymentDetailResponse> failCharge(@Valid final TossFailRequest request) {
		final PaymentDetailDto response = beanPayService.failTossCharge(request);
		return new Response<>(
			HttpStatus.OK.value(),
			PaymentDetailMapper.INSTANCE.toResponse(response)
		);
	}

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
			paymentService.getUserPaymentDetailsByBetweenDate(
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
			paymentService.getSellerPaymentDetailByBetweenRange(
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