package org.ecommerce.paymentapi.external.controller;

import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;

import org.ecommerce.common.security.AuthDetails;
import org.ecommerce.common.security.custom.CurrentUser;
import org.ecommerce.common.vo.Response;
import org.ecommerce.paymentapi.dto.PaymentDetailDto;
import org.ecommerce.paymentapi.dto.PaymentDetailMapper;
import org.ecommerce.paymentapi.dto.request.PreChargeRequest;
import org.ecommerce.paymentapi.dto.request.TossFailRequest;
import org.ecommerce.paymentapi.dto.request.TossPaymentRequest;
import org.ecommerce.paymentapi.dto.response.PaymentDetailResponse;
import org.ecommerce.paymentapi.external.service.BeanPayService;
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

	@PostMapping("/charge")
	public Response<PaymentDetailResponse> beforeCharge(
		@CurrentUser AuthDetails auth,
		@RequestBody final PreChargeRequest request) {
		final PaymentDetailDto response =
			beanPayService.beforeCharge(auth.getId(), request);
		return new Response<>(
			HttpStatus.OK.value(),
			PaymentDetailMapper.INSTANCE.toResponse(response)
		);
	}

	@GetMapping("/success")
	public Response<PaymentDetailResponse> validCharge(
		@Valid final TossPaymentRequest request) {
		//TODO: Jwt 회원 Id 적용 예정
		final PaymentDetailResponse response =
			PaymentDetailMapper.INSTANCE.toResponse(beanPayService.validTossCharge(request));
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



}