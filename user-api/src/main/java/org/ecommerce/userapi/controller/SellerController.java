package org.ecommerce.userapi.controller;

import java.util.Set;

import org.ecommerce.common.vo.Response;
import org.ecommerce.userapi.dto.SellerDto;
import org.ecommerce.userapi.security.AuthDetails;
import org.ecommerce.userapi.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
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

	// TODO : 계좌, 주소 테이블 생성 로직 구현
	//  AccessToken 만료시 RefreshToken 을 통해 Reissue 로직 구현

	private final SellerService sellerService;

	@PostMapping()
	public Response<SellerDto.Response.Register> register(@RequestBody final SellerDto.Request.Register register) {
		final SellerDto responseSeller =  sellerService.registerRequest(register);
		return new Response<>(HttpStatus.OK.value(), SellerDto.Response.Register.of(responseSeller));
	}
	@PostMapping("/login")
	public Response<SellerDto.Response.Login> login(@RequestBody final SellerDto.Request.Login login) {
		final SellerDto responseLogin = sellerService.loginRequest(login);
		return new Response<>(HttpStatus.OK.value(),SellerDto.Response.Login.of(responseLogin));
	}
	@PostMapping("/logout")
	public Response<String> logout(@AuthenticationPrincipal final AuthDetails authDetails){
		sellerService.logoutRequest(authDetails);
		return new Response<>(HttpStatus.OK.value(), "로그아웃에 성공하였습니다");
	}

	/**
	 * 인증 인가 테스트 코드입니다.
	 */
	@GetMapping("/test")
	public Response<String> test(@AuthenticationPrincipal AuthDetails authDetails){
		Set<SimpleGrantedAuthority> authoritySet = authDetails.getAuthoritySet();
		System.out.println("authoritySet = " + authoritySet);
		return new Response<>(HttpStatus.OK.value(), "성공");
	}
}
