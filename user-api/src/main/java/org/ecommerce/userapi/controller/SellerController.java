package org.ecommerce.userapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.AccountDto;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.security.custom.CurrentUser;
import org.ecommerce.userapi.service.SellerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/sellers/v1")
public class SellerController {

	// TODO : 계좌, 주소 테이블 생성 로직 구현
	//  AccessToken 만료시 RefreshToken 을 통해 Reissue 로직 구현

	private final SellerService sellerService;

	@PostMapping()
	public Response<SellerDto.Response.Register> register(@RequestBody final SellerDto.Request.Register seller) {
		final SellerDto responseSeller =  sellerService.registerRequest(seller);
		return new Response<>(HttpStatus.OK.value(), SellerDto.Response.Register.of(responseSeller));
	}
	@PostMapping("/login")
	public Response<SellerDto.Response.Login> login(
		@RequestBody final SellerDto.Request.Login login,
		HttpServletResponse response
	) {
		final SellerDto responseLogin = sellerService.loginRequest(login,response);
		return new Response<>(HttpStatus.OK.value(),SellerDto.Response.Login.of(responseLogin));
	}
	@PostMapping("/logout")
	public Response<String> logout(@CurrentUser final AuthDetails authDetails){
		sellerService.logoutRequest(authDetails);
		return new Response<>(HttpStatus.OK.value(), "로그아웃에 성공하였습니다");
	}

	@PostMapping("/account")
	public Response<AccountDto.Response.Register> account(
		@CurrentUser final AuthDetails authDetails,
		@RequestBody @Valid final AccountDto.Request.Register account){
		AccountDto accountDto = sellerService.registerAccount(authDetails, account);
		return new Response<>(HttpStatus.OK.value(),AccountDto.Response.Register.of(accountDto));
	}
	@PostMapping("/reissue")
	public Response<SellerDto.Response.Login> reissueAccessToken(
		 @RequestHeader(HttpHeaders.AUTHORIZATION) final String bearerToken,
		 HttpServletResponse response
	) {
		SellerDto sellerDto = sellerService.reissueAccessToken(bearerToken, response);
		return new Response<>(HttpStatus.OK.value(), SellerDto.Response.Login.of(sellerDto));
	}
}
