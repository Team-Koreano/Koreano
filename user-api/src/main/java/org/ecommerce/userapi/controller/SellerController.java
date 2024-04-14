package org.ecommerce.userapi.controller;

import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.service.SellerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/sellers/v1")
public class SellerController {
	private final SellerService sellerService;

	@PostMapping()
	public Response<SellerDto.Response.Register> register(@RequestBody SellerDto.Request.Register register) {
		SellerDto.Response.Register responseSeller = sellerService.registerRequest(register);
		return new Response<>(HttpStatus.OK.value(), responseSeller);
	}
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody SellerDto.Request.Login login) {
		SellerDto.Response.Login responseLogin = sellerService.loginRequest(login);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.AUTHORIZATION,  responseLogin.accessToken());
		return ResponseEntity.ok().headers(httpHeaders).body("로그인 되었습니다");
	}
	@PostMapping("/logout")
	public Response<?> logout(@AuthenticationPrincipal AuthDetails authDetails){
		sellerService.logoutRequest(authDetails);
		return new Response<>(HttpStatus.OK.value(), "로그아웃에 성공하였습니다");
	}
}
