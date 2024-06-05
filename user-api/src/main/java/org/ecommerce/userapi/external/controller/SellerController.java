package org.ecommerce.userapi.external.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.AccountDto;
import org.ecommerce.userapi.dto.AccountMapper;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.dto.SellerMapper;
import org.ecommerce.userapi.dto.request.CreateAccountRequest;
import org.ecommerce.userapi.dto.request.CreateSellerRequest;
import org.ecommerce.userapi.dto.request.LoginSellerRequest;
import org.ecommerce.userapi.dto.request.WithdrawalSellerRequest;
import org.ecommerce.userapi.dto.response.CreateAccountResponse;
import org.ecommerce.userapi.dto.response.CreateSellerResponse;
import org.ecommerce.userapi.dto.response.LoginSellerResponse;
import org.ecommerce.userapi.external.service.SellerService;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.security.custom.CurrentUser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/external/sellers/v1")
public class SellerController {

	// TODO : 계좌, 주소 테이블 생성 로직 구현
	//  AccessToken 만료시 RefreshToken 을 통해 Reissue 로직 구현

	private final SellerService sellerService;

	@PostMapping()
	public Response<CreateSellerResponse> register(@RequestBody final CreateSellerRequest seller) {
		final SellerDto responseSeller = sellerService.registerRequest(seller);
		return new Response<>(HttpStatus.OK.value(), SellerMapper.INSTANCE.toResponse(responseSeller));
	}

	@PostMapping("/login")
	public Response<LoginSellerResponse> login(
		@RequestBody final LoginSellerRequest login,
		HttpServletResponse response
	) {
		final SellerDto responseLogin = sellerService.loginRequest(login, response);
		return new Response<>(HttpStatus.OK.value(), LoginSellerResponse.of(responseLogin));
	}

	@PostMapping("/logout")
	public Response<String> logout(@CurrentUser final AuthDetails authDetails) {
		sellerService.logoutRequest(authDetails);
		return new Response<>(HttpStatus.OK.value(), "로그아웃에 성공하였습니다");
	}

	@PostMapping("/account")
	public Response<CreateAccountResponse> account(
		@CurrentUser final AuthDetails authDetails,
		@RequestBody @Valid final CreateAccountRequest account) {
		final AccountDto accountDto = sellerService.registerAccount(authDetails, account);
		return new Response<>(HttpStatus.OK.value(), AccountMapper.INSTANCE.toResponse(accountDto));
	}

	@PostMapping("/reissue")
	public Response<LoginSellerResponse> reissueAccessToken(
		@RequestHeader(HttpHeaders.AUTHORIZATION) final String bearerToken,
		HttpServletResponse response
	) {
		final SellerDto sellerDto = sellerService.reissueAccessToken(bearerToken, response);
		return new Response<>(HttpStatus.OK.value(), LoginSellerResponse.of(sellerDto));
	}

	@DeleteMapping()
	public Response<String> withdrawSeller(
		@CurrentUser final AuthDetails authDetails,
		@Valid @RequestBody final WithdrawalSellerRequest withdrawal
	) {
		sellerService.withdrawSeller(withdrawal, authDetails);
		return new Response<>(HttpStatus.OK.value(), "탈퇴에 성공하였습니다");
	}
}
